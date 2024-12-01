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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The stock addition view.
 */

public final class CAGStockAddView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStringsType strings;
  private final CAGItemSearchControllerType itemSearch;
  private final CAGLocationTreeControllerType locationSearch;
  private final CAGItemSelectDialogs itemSelectDialogs;
  private final CAGLocationSelectDialogs locationSelectDialogs;
  private final CAGItemDetailsControllerType itemDetails;
  private final CAGStockSearchControllerType stock;

  @FXML private TextField itemField;
  @FXML private TextField locationField;
  @FXML private Button itemSelectButton;
  @FXML private Button locationSelectButton;
  @FXML private Button addButton;
  @FXML private ChoiceBox<CAGStockKind> stockKind;
  @FXML private TextField serialTypeField;
  @FXML private TextField serialValueField;
  @FXML private Spinner<Long> countSpinner;

  /**
   * The stock addition view.
   *
   * @param inStage   The stage
   * @param services  The services
   * @param arguments The arguments
   */

  public CAGStockAddView(
    final Stage inStage,
    final RPServiceDirectoryType services,
    final CAGStockAddDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.strings =
      services.requireService(CAGStringsType.class);
    this.stock =
      arguments.stockController();
    this.itemSearch =
      arguments.itemSearchController();
    this.itemDetails =
      arguments.itemDetailsController();
    this.locationSearch =
      arguments.locationController();
    this.itemSelectDialogs =
      services.requireService(CAGItemSelectDialogs.class);
    this.locationSelectDialogs =
      services.requireService(CAGLocationSelectDialogs.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.serialTypeField.setText("manufacturer_serial");

    this.addButton.setDisable(true);

    this.stockKind.getSelectionModel()
      .selectedItemProperty()
      .addListener((_, _, newValue) -> this.onStockKindChanged(newValue));

    this.stockKind.setItems(
      FXCollections.observableArrayList(CAGStockKind.values())
    );

    this.stockKind.getSelectionModel()
      .select(CAGStockKind.SET);

    this.countSpinner.setValueFactory(
      new CAGSpinnerLongFactory());

    this.itemField.textProperty()
      .addListener(_ -> this.validate());
    this.locationField.textProperty()
      .addListener(_ -> this.validate());
    this.serialTypeField.textProperty()
      .addListener(_ -> this.validate());
    this.serialValueField.textProperty()
      .addListener(_ -> this.validate());
    this.countSpinner.getValueFactory()
      .valueProperty()
      .addListener(_ -> this.validate());
    this.stockKind.getSelectionModel()
      .selectedItemProperty()
      .addListener(_ -> this.validate());
  }

  private void validate()
  {
    var ok = true;
    ok = ok && !this.itemField.getText().isBlank();
    ok = ok && !this.locationField.getText().isBlank();

    switch (this.stockKind.getValue()) {
      case SERIAL -> {
        ok = ok && !this.serialTypeField.getText().isBlank();
        ok = ok && !this.serialValueField.getText().isBlank();
      }
      case SET -> {
        ok = ok && (this.countSpinner.getValue().longValue() > 0L);
      }
    }

    this.addButton.setDisable(!ok);
  }

  private void onStockKindChanged(
    final CAGStockKind newValue)
  {
    switch (newValue) {
      case SERIAL -> {
        this.countSpinner.setVisible(false);
        this.serialTypeField.setVisible(true);
        this.serialValueField.setVisible(true);
      }
      case SET -> {
        this.countSpinner.setVisible(true);
        this.serialTypeField.setVisible(false);
        this.serialValueField.setVisible(false);
      }
      case null -> {

      }
    }
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onStockAddSelected()
  {
    switch (this.stockKind.getValue()) {
      case SERIAL -> {
        this.stock.stockIntroduceSerial(
          CAStockInstanceID.random(),
          CALocationID.of(this.locationField.getText()),
          CAItemID.of(this.itemField.getText()),
          new CAItemSerial(
            new RDottedName(this.serialTypeField.getText().trim()),
            this.serialValueField.getText().trim()
          )
        );
      }
      case SET -> {
        this.stock.stockIntroduceSet(
          CAStockInstanceID.random(),
          CALocationID.of(this.locationField.getText()),
          CAItemID.of(this.itemField.getText()),
          this.countSpinner.getValue().longValue()
        );
      }
    }
  }

  @FXML
  private void onItemSelect()
    throws IOException
  {
    this.itemSelectDialogs.openDialogAndWait(
      new CAGItemSelectDialogArguments(
        this.itemDetails,
        this.itemSearch
      )
    );

    final var itemOpt =
      this.itemDetails.itemSelected()
        .summary()
        .getValue();

    itemOpt.ifPresent(summary -> {
      this.itemField.setText(summary.id().displayId());
    });
  }

  @FXML
  private void onLocationSelect()
    throws IOException
  {
    this.locationSelectDialogs.openDialogAndWait(
      this.locationSearch
    );

    final var locationOpt =
      this.locationSearch.locationSelected()
        .summary()
        .getValue();

    locationOpt.ifPresent(summary -> {
      this.locationField.setText(summary.id().displayId());
    });
  }
}
