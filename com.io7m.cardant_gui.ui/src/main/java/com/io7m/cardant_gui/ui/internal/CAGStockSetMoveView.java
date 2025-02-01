/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.cardant_gui.ui.internal;

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCKMOVE_KINDEXISTING;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCKMOVE_KINDNEW;

/**
 * The stock set move view.
 */

public final class CAGStockSetMoveView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStockSearchControllerType stock;
  private final CAStockOccurrenceSet instance;
  private final CAGStringsType strings;
  private final CAGLocationSelectDialogs locationSelectDialogs;
  private final RPServiceDirectoryType services;
  private final CAGClientServiceType client;
  private final CAGLocationTreeControllerType locationTreeController;
  private final CAGSpinnerUnsignedLongFactory stockMoveCountValues;
  private final CAGStockSelectDialogs stockSearchDialogs;

  @FXML private Parent root;
  @FXML private Button moveButton;
  @FXML private Spinner<Long> stockMoveCount;
  @FXML private TextField stockAvailableCount;
  @FXML private TextField stockLocationField;
  @FXML private TextField stockInstanceSource;
  @FXML private TextField stockInstanceTarget;
  @FXML private Button stockInstanceTargetSelect;
  @FXML private Button stockInstanceLocationSelect;
  @FXML private ChoiceBox<MoveKind> stockMoveKind;

  /**
   * The stock addition view.
   *
   * @param inStage   The stage
   * @param arguments The arguments
   */

  public CAGStockSetMoveView(
    final Stage inStage,
    final CAGStockSetMoveDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.services =
      arguments.services();
    this.stock =
      arguments.stockController();
    this.instance =
      arguments.stockInstance();

    this.stockSearchDialogs =
      this.services.requireService(CAGStockSelectDialogs.class);
    this.locationSelectDialogs =
      this.services.requireService(CAGLocationSelectDialogs.class);
    this.strings =
      this.services.requireService(CAGStringsType.class);
    this.client =
      this.services.requireService(CAGClientServiceType.class);
    this.locationTreeController =
      CAGLocationTreeController.create(this.client);
    this.stockMoveCountValues =
      new CAGSpinnerUnsignedLongFactory();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.stockInstanceSource.setText(this.instance.instance().displayId());

    this.stockMoveKind.getSelectionModel()
      .selectedItemProperty()
      .addListener((_, _, newValue) -> {
        switch (newValue) {
          case CREATE_NEW_INSTANCE -> {
            this.stockInstanceTargetSelect.setDisable(true);
            this.stockInstanceTarget.setText(
              CAStockInstanceID.random().displayId()
            );
          }
          case MOVE_TO_EXISTING_INSTANCE -> {
            this.stockInstanceTargetSelect.setDisable(false);
            this.stockInstanceTarget.setDisable(false);
          }
        }
      });

    this.stockMoveKind.setItems(
      FXCollections.observableArrayList(MoveKind.values()));
    this.stockMoveKind.setConverter(
      new MoveKindStringConverter(this.strings));

    this.stockMoveKind.getSelectionModel()
      .select(0);

    this.stockAvailableCount.setText(
      Long.toUnsignedString(this.instance.count()));

    this.stockMoveCount.setValueFactory(
      this.stockMoveCountValues);

    this.stockMoveCount.valueProperty()
      .addListener((_, _, newValue) -> {
        this.stockMoveCountValues.setValue(
          Math.clamp(newValue, 1L, this.instance.count())
        );
      });

    this.stockMoveCountValues.setValue(this.instance.count());
  }

  private void validate()
  {
    var ok = true;
    this.moveButton.setDisable(true);

    try {
      CALocationID.of(this.stockLocationField.getText());
    } catch (final Throwable e) {
      ok = false;
    }

    switch (this.stockMoveKind.getValue()) {
      case CREATE_NEW_INSTANCE -> {

      }
      case MOVE_TO_EXISTING_INSTANCE -> {
        try {
          CAStockInstanceID.of(this.stockInstanceTarget.getText());
        } catch (final Throwable e) {
          ok = false;
        }
      }
    }

    this.moveButton.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onStockLocationSelect()
    throws IOException
  {
    this.locationSelectDialogs.openDialogAndWait(this.locationTreeController);

    final var locationOpt =
      this.locationTreeController.locationSelected()
        .summary()
        .getValue();

    locationOpt.ifPresent(summary -> {
      this.stockLocationField.setText(summary.id().displayId());
    });

    this.validate();
  }

  @FXML
  private void onStockInstanceSelect()
    throws IOException
  {
    this.stockSearchDialogs.openDialogAndWait(
      new CAGStockSelectDialogArguments(
        this.services,
        this.stock,
        CAGStockSelectRestriction.SET_OCCURRENCES
      )
    );

    final var r =
      this.stock.stockSelected().getValue();

    r.ifPresent(occurrence -> {
      this.stockInstanceTarget.setText(occurrence.instance().displayId());
    });

    this.validate();
  }

  @FXML
  private void onStockSetMoveSelected()
  {
    this.root.setDisable(true);

    final var future =
      this.stock.stockSetMove(
        new CAStockRepositSetMove(
          CAStockInstanceID.of(this.stockInstanceSource.getText()),
          CAStockInstanceID.of(this.stockInstanceTarget.getText()),
          CALocationID.of(this.stockLocationField.getText()),
          this.stockMoveCount.getValue().longValue()
        )
      );

    future.whenComplete((_, exception) -> {
      Platform.runLater(() -> this.root.setDisable(false));

      if (exception == null) {
        Platform.runLater(this.stage::close);
      } else {
        Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
      }
    });
  }

  private enum MoveKind
  {
    CREATE_NEW_INSTANCE,
    MOVE_TO_EXISTING_INSTANCE
  }

  private static final class MoveKindStringConverter
    extends StringConverter<MoveKind>
  {
    private final CAGStringsType strings;

    private MoveKindStringConverter(
      final CAGStringsType inStrings)
    {
      this.strings =
        Objects.requireNonNull(inStrings, "strings");
    }

    @Override
    public String toString(
      final MoveKind object)
    {
      return switch (object) {
        case CREATE_NEW_INSTANCE -> {
          yield this.strings.format(CARDANT_STOCKMOVE_KINDNEW);
        }
        case MOVE_TO_EXISTING_INSTANCE -> {
          yield this.strings.format(CARDANT_STOCKMOVE_KINDEXISTING);
        }
      };
    }

    @Override
    public MoveKind fromString(
      final String string)
    {
      return null;
    }
  }
}
