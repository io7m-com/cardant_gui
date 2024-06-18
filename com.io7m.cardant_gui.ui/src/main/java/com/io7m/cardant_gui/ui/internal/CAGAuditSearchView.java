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

import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CATimeRange;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The main audit search view.
 */

public final class CAGAuditSearchView
  implements CAGViewType
{
  private static final StringConverter<LocalDate> ISO_DATE_CONVERTER =
    new CAGISODateConverter();

  private CAGAuditControllerType controller;
  private final CAGStringsType strings;

  @FXML private DatePicker dateLower;
  @FXML private Spinner<OffsetDateTime> timeLower;
  @FXML private DatePicker dateUpper;
  @FXML private Spinner<OffsetDateTime> timeUpper;
  @FXML private ChoiceBox<CAGAuditEventTypeMatchKind> typeMatch;
  @FXML private TextField typeName;
  @FXML private TextField owner;
  @FXML private Accordion accordion;
  @FXML private TitledPane basicParameters;

  /**
   * The main audit search view.
   *
   * @param services The service directory
   */

  public CAGAuditSearchView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
  }

  /**
   * Set the controllers.
   *
   * @param inController The controller
   */

  public void setControllers(
    final CAGAuditControllerType inController)
  {
    this.controller =
      Objects.requireNonNull(inController, "controller");
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.accordion.setExpandedPane(this.basicParameters);

    this.typeMatch.setItems(
      FXCollections.observableArrayList(CAGAuditEventTypeMatchKind.values()));
    this.typeMatch.setConverter(
      new CAGAuditEventTypeMatchConverter(this.strings));
    this.typeMatch.getSelectionModel()
      .select(CAGAuditEventTypeMatchKind.ANY);
    this.typeMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onTypeMatchChanged(newValue);
      });

    final var timeNow =
      OffsetDateTime.now().plusDays(1L);
    final var timeThen =
      timeNow.minusWeeks(1L);

    final var lowerTimeFactory =
      new CAGSpinnerTimeValueFactory();
    final var upperTimeFactory =
      new CAGSpinnerTimeValueFactory();
    final var timeConverter =
      new CAGSpinnerTimeOnlyStringConverter();

    lowerTimeFactory.setConverter(timeConverter);
    upperTimeFactory.setConverter(timeConverter);
    lowerTimeFactory.setTime(timeThen);
    upperTimeFactory.setTime(timeNow);

    this.timeLower.setValueFactory(lowerTimeFactory);
    this.timeUpper.setValueFactory(upperTimeFactory);

    this.dateLower.setConverter(ISO_DATE_CONVERTER);
    this.dateUpper.setConverter(ISO_DATE_CONVERTER);
    this.dateLower.setValue(timeThen.toLocalDate());
    this.dateUpper.setValue(timeNow.toLocalDate());
  }

  private void onTypeMatchChanged(
    final CAGAuditEventTypeMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.typeName.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO -> {
        this.typeName.setDisable(false);
      }
    }
  }

  @FXML
  private void onSearchSelected()
  {
    final var lowerDate =
      this.dateLower.getValue();
    final var upperDate =
      this.dateUpper.getValue();
    final var lowerTime =
      this.timeLower.getValue().toLocalTime();
    final var upperTime =
      this.timeUpper.getValue().toLocalTime();

    final var lowerDateTime =
      OffsetDateTime.of(
        lowerDate,
        lowerTime,
        ZoneOffset.UTC
      );
    final var upperDateTime =
      OffsetDateTime.of(
        upperDate,
        upperTime,
        ZoneOffset.UTC
      );

    Optional<CAUserID> ownerId;
    final var ownerText = this.owner.getText().trim();
    if (!ownerText.isEmpty()) {
      try {
        ownerId = Optional.of(CAUserID.of(ownerText));
      } catch (final Exception e) {
        ownerId = Optional.empty();
      }
    } else {
      ownerId = Optional.empty();
    }

    this.controller.auditSearchBegin(
      new CAAuditSearchParameters(
        ownerId,
        this.typeMatch(),
        new CATimeRange(lowerDateTime, upperDateTime),
        25L
      )
    );
  }

  private CAComparisonExactType<String> typeMatch()
  {
    return switch (this.typeMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case EQUAL_TO -> {
        yield new CAComparisonExactType.IsEqualTo<>(
          this.typeName.getText().trim()
        );
      }
      case NOT_EQUAL_TO -> {
        yield new CAComparisonExactType.IsNotEqualTo<>(
          this.typeName.getText().trim()
        );
      }
    };
  }

  @FXML
  private void onSearchClearSelected()
  {

  }
}
