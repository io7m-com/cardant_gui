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

import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringAnything;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringNotEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueAnything;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueExactText;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueIntegerWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueMoneyWithCurrency;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueMoneyWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueRealWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueSearchText;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueTimeWithinRange;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;
import org.joda.money.CurrencyUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_AND;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_COMPARISON_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_COMPARISON_EQUALTO;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_COMPARISON_NOTEQUALTO;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_OR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_SPECIFIC;

/**
 * A tree cell controller.
 */

public final class CAGMetaMatchCellController implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMetaMatchCellController.class);

  private static final StringConverter<LocalDate> ISO_DATE_CONVERTER =
    new CAGISODateConverter();

  private final CAGStringsType strings;
  private final CAGMetaMatchTreeSequenceType compilations;
  private CloseableCollectionType<IllegalStateException> resources;

  @FXML private Parent root;
  @FXML private Parent stringComparison;
  @FXML private TextField stringComparisonField;
  @FXML private Label stringComparisonFieldLabel;
  @FXML private Label stringComparisonFieldOp;
  @FXML private Parent element;
  @FXML private Label elementLabel;
  @FXML private Parent valueAny;
  @FXML private Label valueAnyLabel;
  @FXML private Parent valueExactText;
  @FXML private TextField valueExactTextField;
  @FXML private Parent valueSearchText;
  @FXML private TextField valueSearchTextField;
  @FXML private Parent valueIntegralRange;
  @FXML private TextField valueIntegralRange0;
  @FXML private TextField valueIntegralRange1;
  @FXML private Parent valueRealRange;
  @FXML private TextField valueRealRange0;
  @FXML private TextField valueRealRange1;
  @FXML private Parent valueTimeRange;
  @FXML private DatePicker valueTimeRangeDate0;
  @FXML private Spinner<OffsetDateTime> valueTimeRangeTime0;
  @FXML private DatePicker valueTimeRangeDate1;
  @FXML private Spinner<OffsetDateTime> valueTimeRangeTime1;
  @FXML private Parent valueMoneyRange;
  @FXML private TextField valueMoneyRange0;
  @FXML private TextField valueMoneyRange1;
  @FXML private Parent valueMoneyCurrency;
  @FXML private ComboBox<CurrencyUnit> valueMoneyCurrencyBox;

  private List<Parent> containers;

  /**
   * Construct a cell controller.
   *
   * @param inStrings      The string resources
   * @param inCompilations The compilation sequence
   */

  public CAGMetaMatchCellController(
    final CAGStringsType inStrings,
    final CAGMetaMatchTreeSequenceType inCompilations)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.compilations =
      Objects.requireNonNull(inCompilations, "compilations");

    this.resources = createCloseable();
  }

  private static CloseableCollectionType<IllegalStateException> createCloseable()
  {
    return CloseableCollection.create(
      () -> {
        return new IllegalStateException("A resource could not be closed.");
      });
  }

  void unsetItem()
  {
    LOG.trace(
      "[{}] unsetItem",
      Integer.toUnsignedString(this.hashCode(), 16)
    );

    for (final var e : this.containers) {
      e.setVisible(false);
    }

    this.resources.close();
    this.resources = createCloseable();
  }

  void setItem(
    final CAGMetaMatchNodeType item)
  {
    LOG.trace(
      "[{}] setItem ({})",
      Integer.toUnsignedString(this.hashCode(), 16),
      item
    );

    for (final var e : this.containers) {
      e.setVisible(false);
    }

    final var s = this.strings;
    switch (item) {
      case final Element e -> {
        this.setItemElement(e, s);
      }

      case final StringComparisonNodeType c -> {
        this.setItemStringComparison(c, s);
      }

      case final ValueType v -> {
        this.setItemValue(v);
      }
    }
  }

  private void setItemValue(final ValueType v)
  {
    switch (v) {
      case final ValueAnything va -> {
        this.valueAny.setVisible(true);
      }

      case final ValueExactText va -> {
        this.valueExactText.setVisible(true);
        this.valueExactTextField.setText(va.text().get());
        this.bindTextInputListener(va.text(), this.valueExactTextField);
      }

      case final ValueIntegerWithinRange va -> {
        this.valueIntegralRange.setVisible(true);
        this.valueIntegralRange0.setText(va.lower().getValue().toString());
        this.valueIntegralRange1.setText(va.upper().getValue().toString());
        this.bindLongInputListener(va.lower(), this.valueIntegralRange0);
        this.bindLongInputListener(va.upper(), this.valueIntegralRange1);
      }

      case final ValueMoneyWithCurrency va -> {
        this.valueMoneyCurrency.setVisible(true);
        this.valueMoneyCurrencyBox.getSelectionModel()
          .select(va.unit().get());

        this.bindCurrencyInputListener(va.unit(), this.valueMoneyCurrencyBox);
      }

      case final ValueMoneyWithinRange va -> {
        this.valueMoneyRange.setVisible(true);
        this.valueMoneyRange0.setText(va.lower().getValue().toString());
        this.valueMoneyRange1.setText(va.upper().getValue().toString());
        this.bindMoneyInputListener(va.lower(), this.valueMoneyRange0);
        this.bindMoneyInputListener(va.upper(), this.valueMoneyRange1);
      }

      case final ValueRealWithinRange va -> {
        this.valueRealRange.setVisible(true);
        this.valueRealRange0.setText(va.lower().getValue().toString());
        this.valueRealRange1.setText(va.upper().getValue().toString());
        this.bindRealInputListener(va.lower(), this.valueRealRange0);
        this.bindRealInputListener(va.upper(), this.valueRealRange1);
      }

      case final ValueSearchText va -> {
        this.valueSearchText.setVisible(true);
        this.valueSearchTextField.setText(va.text().getValue());
        this.bindTextInputListener(va.text(), this.valueSearchTextField);
      }

      case final ValueTimeWithinRange va -> {
        this.valueTimeRange.setVisible(true);

        final var timeLower =
          va.lower().getValue();
        final var timeUpper =
          va.upper().getValue();

        final var lowerTimeFactory =
          new CAGSpinnerTimeValueFactory();
        final var upperTimeFactory =
          new CAGSpinnerTimeValueFactory();
        final var timeConverter =
          new CAGSpinnerTimeOnlyStringConverter();

        lowerTimeFactory.setConverter(timeConverter);
        upperTimeFactory.setConverter(timeConverter);
        lowerTimeFactory.setTime(timeLower);
        upperTimeFactory.setTime(timeUpper);

        this.valueTimeRangeTime0.setValueFactory(lowerTimeFactory);
        this.valueTimeRangeTime1.setValueFactory(upperTimeFactory);
        this.valueTimeRangeDate0.setValue(timeLower.toLocalDate());
        this.valueTimeRangeDate1.setValue(timeUpper.toLocalDate());

        this.bindDateInputListener(
          va.lower(),
          this.valueTimeRangeDate0,
          lowerTimeFactory
        );
        this.bindDateInputListener(
          va.upper(),
          this.valueTimeRangeDate1,
          upperTimeFactory
        );
      }
    }
  }

  private void setItemStringComparison(
    final StringComparisonNodeType c,
    final CAGStringsType s)
  {
    this.stringComparison.setVisible(true);

    this.stringComparisonFieldLabel.setText(c.fieldName());
    this.stringComparisonField.setText(c.value().getValue());
    this.bindTextInputListener(c.value(), this.stringComparisonField);

    this.stringComparisonFieldOp.setText(
      switch (c) {
        case final StringAnything a ->
          s.format(CARDANT_ITEMSEARCH_METADATA_COMPARISON_ANY);
        case final StringEqualTo e ->
          s.format(CARDANT_ITEMSEARCH_METADATA_COMPARISON_EQUALTO);
        case final StringNotEqualTo n ->
          s.format(CARDANT_ITEMSEARCH_METADATA_COMPARISON_NOTEQUALTO);
      }
    );

    this.stringComparisonField.setVisible(
      switch (c) {
        case final StringAnything a -> false;
        case final StringEqualTo e -> true;
        case final StringNotEqualTo n -> true;
      }
    );
  }

  private void setItemElement(
    final Element e,
    final CAGStringsType s)
  {
    this.element.setVisible(true);

    this.elementLabel.setText(
      switch (e) {
        case ANYTHING -> s.format(CARDANT_ITEMSEARCH_METADATA_ANY);
        case OR -> s.format(CARDANT_ITEMSEARCH_METADATA_OR);
        case AND -> s.format(CARDANT_ITEMSEARCH_METADATA_AND);
        case MATCH -> s.format(CARDANT_ITEMSEARCH_METADATA_SPECIFIC);
      }
    );
  }

  private void bindCurrencyInputListener(
    final SimpleObjectProperty<CurrencyUnit> p,
    final ComboBox<CurrencyUnit> control)
  {
    final ChangeListener<CurrencyUnit> listener =
      (observable, oldValue, newValue) -> {
        p.set(newValue);
        this.compilations.update();
      };

    this.resources.add(() -> {
      control.valueProperty().removeListener(listener);
    });
    control.valueProperty().addListener(listener);
  }

  private void bindDateInputListener(
    final SimpleObjectProperty<OffsetDateTime> p,
    final DatePicker datePicker,
    final CAGSpinnerTimeValueFactory timeFactory)
  {
    final ChangeListener<LocalDate> dateListener =
      (ignored0, ignored1, ignored2) -> {
        final var date =
          datePicker.valueProperty()
            .get();

        final var time =
          timeFactory.getValue()
            .toLocalTime();

        p.set(OffsetDateTime.of(date, time, ZoneOffset.UTC));
        this.compilations.update();
      };

    final ChangeListener<OffsetDateTime> timeListener =
      (ignored0, ignored1, ignored2) -> {
        final var date =
          datePicker.valueProperty()
            .get();

        final var time =
          timeFactory.getValue()
            .toLocalTime();

        p.set(OffsetDateTime.of(date, time, ZoneOffset.UTC));
        this.compilations.update();
      };

    this.resources.add(() -> {
      datePicker.valueProperty().removeListener(dateListener);
    });
    this.resources.add(() -> {
      timeFactory.valueProperty().removeListener(timeListener);
    });

    datePicker.valueProperty().addListener(dateListener);
    timeFactory.valueProperty().addListener(timeListener);
  }

  private void bindTextInputListener(
    final StringProperty p,
    final TextInputControl control)
  {
    final ChangeListener<String> listener =
      (observable, oldValue, newValue) -> {
        p.set(newValue);
        this.compilations.update();
      };

    this.resources.add(() -> {
      control.textProperty().removeListener(listener);
    });
    control.textProperty().addListener(listener);
  }

  private void bindLongInputListener(
    final LongProperty p,
    final TextInputControl control)
  {
    final ChangeListener<String> listener =
      (observable, oldValue, newValue) -> {
        try {
          p.set(Long.parseLong(newValue));
        } catch (final NumberFormatException e) {
          LOG.error("Invalid number: {}", e.getMessage());
        }
        this.compilations.update();
      };

    this.resources.add(() -> control.textProperty().removeListener(listener));
    control.textProperty().addListener(listener);
  }

  private void bindRealInputListener(
    final DoubleProperty p,
    final TextInputControl control)
  {
    final ChangeListener<String> listener =
      (observable, oldValue, newValue) -> {
        try {
          p.set(Double.parseDouble(newValue));
        } catch (final NumberFormatException e) {
          LOG.error("Invalid number: {}", e.getMessage());
        }
        this.compilations.update();
      };

    this.resources.add(() -> control.textProperty().removeListener(listener));
    control.textProperty().addListener(listener);
  }

  private void bindMoneyInputListener(
    final SimpleObjectProperty<BigDecimal> p,
    final TextInputControl control)
  {
    final ChangeListener<String> listener =
      (observable, oldValue, newValue) -> {
        try {
          p.set(new BigDecimal(newValue));
        } catch (final NumberFormatException e) {
          LOG.error("Invalid number: {}", e.getMessage());
        }
        this.compilations.update();
      };

    this.resources.add(() -> control.textProperty().removeListener(listener));
    control.textProperty().addListener(listener);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.containers =
      List.of(
        this.element,
        this.stringComparison,
        this.valueAny,
        this.valueExactText,
        this.valueIntegralRange,
        this.valueMoneyCurrency,
        this.valueMoneyRange,
        this.valueRealRange,
        this.valueSearchText,
        this.valueTimeRange
      );

    this.valueTimeRangeDate0.setConverter(ISO_DATE_CONVERTER);
    this.valueTimeRangeDate1.setConverter(ISO_DATE_CONVERTER);
    this.valueMoneyCurrencyBox.setItems(
      FXCollections.observableList(CurrencyUnit.registeredCurrencies())
    );
  }
}
