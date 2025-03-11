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
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The metadata addition view.
 */

public final class CAGMetadataAddView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGItemDetailsControllerType itemController;
  private final CAItemID item;

  @FXML private Parent root;
  @FXML private TextField packageField;
  @FXML private TextField typeField;
  @FXML private TextField fieldField;
  @FXML private Button addButton;
  @FXML private TextArea errorText;
  @FXML private ChoiceBox<CAMetadataValueKind> kind;
  @FXML private Spinner<Long> valueIntegerField;
  @FXML private DatePicker valueDateField;
  @FXML private TextField valueTimeField;
  @FXML private TextField valueMoneyCurrencyField;
  @FXML private TextField valueMoneyValueField;
  @FXML private TextField valueTextField;
  @FXML private TextField valueRealField;

  private List<Control> fields;

  /**
   * The metadata addition view.
   *
   * @param inStage   The stage
   * @param services  The services
   * @param arguments The arguments
   */

  public CAGMetadataAddView(
    final Stage inStage,
    final RPServiceDirectoryType services,
    final CAGMetadataAddDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");

    this.item =
      arguments.item();
    this.itemController =
      arguments.itemController();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.errorText.setVisible(false);

    this.fields =
      List.of(
        this.valueIntegerField,
        this.valueDateField,
        this.valueTimeField,
        this.valueMoneyCurrencyField,
        this.valueMoneyValueField,
        this.valueTextField,
        this.valueRealField
      );

    this.kind.setItems(
      FXCollections.observableArrayList(CAMetadataValueKind.values()));
    this.kind.valueProperty()
      .addListener(_ -> this.setFieldVisibility());
    this.kind.setValue(
      CAMetadataValueKind.INTEGRAL);

    this.valueIntegerField.setValueFactory(new CAGSpinnerSignedLongFactory());
    this.valueIntegerField.focusedProperty()
      .addListener((_, _, _) -> {
        try {
          this.valueIntegerField.getValueFactory().setValue(
            Long.parseLong(this.valueIntegerField.getEditor().getText())
          );
        } catch (final Exception e) {
          // Nothing.
        }
      });

    this.packageField.textProperty()
      .addListener(_ -> this.validate());
    this.typeField.textProperty()
      .addListener(_ -> this.validate());
    this.fieldField.textProperty()
      .addListener(_ -> this.validate());

    this.kind.valueProperty()
      .addListener(_ -> this.validate());
    this.valueIntegerField.valueProperty()
      .addListener(_ -> this.validate());
    this.valueDateField.valueProperty()
      .addListener(_ -> this.validate());
    this.valueTimeField.textProperty()
      .addListener(_ -> this.validate());
    this.valueMoneyCurrencyField.textProperty()
      .addListener(_ -> this.validate());
    this.valueMoneyValueField.textProperty()
      .addListener(_ -> this.validate());
    this.valueTextField.textProperty()
      .addListener(_ -> this.validate());
    this.valueRealField.textProperty()
      .addListener(_ -> this.validate());
  }

  private void setFieldVisibility()
  {
    this.fields.forEach(c -> c.setVisible(false));

    switch (this.kind.getValue()) {
      case INTEGRAL -> {
        this.valueIntegerField.setVisible(true);
      }
      case TEXT -> {
        this.valueTextField.setVisible(true);
      }
      case TIME -> {
        this.valueDateField.setVisible(true);
        this.valueTimeField.setVisible(true);
      }
      case MONETARY -> {
        this.valueMoneyCurrencyField.setVisible(true);
        this.valueMoneyValueField.setVisible(true);
      }
      case REAL -> {
        this.valueRealField.setVisible(true);
      }
    }
  }

  private void validate()
  {
    var ok = true;
    try {
      new CATypeRecordFieldIdentifier(
        new CATypeRecordIdentifier(
          new RDottedName(this.packageField.getText().trim()),
          new RDottedName(this.typeField.getText().trim())
        ),
        new RDottedName(this.fieldField.getText().trim())
      );
    } catch (final Exception e) {
      this.errorText.setText(e.getMessage());
      this.errorText.setVisible(true);
      ok = false;
    }

    switch (this.kind.getValue()) {
      case INTEGRAL -> {

      }
      case TEXT -> {

      }
      case TIME -> {
        try {
          LocalTime.parse(this.valueTimeField.getText().trim());
        } catch (final Exception e) {
          this.errorText.setText(
            "Invalid time value: %s".formatted(e.getMessage()));
          this.errorText.setVisible(true);
          ok = false;
        }
      }
      case MONETARY -> {
        try {
          new BigDecimal(this.valueMoneyValueField.getText().trim());
        } catch (final Exception e) {
          this.errorText.setText(
            "Invalid monetary value: %s".formatted(e.getMessage()));
          this.errorText.setVisible(true);
          ok = false;
        }
      }
      case REAL -> {
        try {
          Double.parseDouble(this.valueRealField.getText().trim());
        } catch (final Exception e) {
          this.errorText.setText(
            "Invalid real value: %s".formatted(e.getMessage()));
          this.errorText.setVisible(true);
          ok = false;
        }
      }
    }

    if (ok) {
      this.errorText.setVisible(false);
    }

    this.addButton.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onAddSelected()
  {
    this.root.setDisable(true);

    final var id =
      new CATypeRecordFieldIdentifier(
        new CATypeRecordIdentifier(
          new RDottedName(this.packageField.getText().trim()),
          new RDottedName(this.typeField.getText().trim())
        ),
        new RDottedName(this.fieldField.getText().trim())
      );

    final CAMetadataType meta =
      switch (this.kind.getValue()) {
        case INTEGRAL -> {
          yield new CAMetadataType.Integral(
            id,
            this.valueIntegerField.getValue().longValue()
          );
        }
        case TEXT -> {
          yield new CAMetadataType.Text(
            id,
            this.valueTextField.getText().trim()
          );
        }
        case TIME -> {
          yield new CAMetadataType.Time(
            id,
            OffsetDateTime.of(
              this.valueDateField.getValue(),
              LocalTime.parse(this.valueTimeField.getText().trim()),
              ZoneOffset.UTC
            )
          );
        }
        case MONETARY -> {
          yield new CAMetadataType.Monetary(
            id,
            new BigDecimal(this.valueMoneyValueField.getText().trim()),
            CurrencyUnit.of(this.valueMoneyCurrencyField.getText().trim())
          );
        }
        case REAL -> {
          yield new CAMetadataType.Real(
            id,
            Double.parseDouble(this.valueRealField.getText().trim())
          );
        }
      };

    this.itemController.itemMetadataAdd(this.item, meta)
      .whenComplete((_, exception) -> {
        Platform.runLater(() -> this.root.setDisable(false));

        if (exception == null) {
          Platform.runLater(this.stage::close);
        } else {
          Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
        }
      });
  }
}
