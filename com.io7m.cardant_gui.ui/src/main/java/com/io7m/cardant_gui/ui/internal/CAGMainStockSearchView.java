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

import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_CANCEL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEARTITLE;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 * The main stock search view.
 */

public final class CAGMainStockSearchView
  implements CAGViewType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMainStockSearchView.class);

  private final CAGStringsType strings;
  private final CAGControllerType controller;

  @FXML private ChoiceBox<CAGLocationMatchKind> locationMatch;
  @FXML private TextField locationField;
  @FXML private ChoiceBox<CAGItemIDMatchKind> idMatch;
  @FXML private TextField idField;
  @FXML private CheckBox includeSerial;
  @FXML private CheckBox includeSets;
  @FXML private TitledPane basicParameters;
  @FXML private Accordion accordion;

  /**
   * The main stock search view.
   *
   * @param services The service directory
   */

  public CAGMainStockSearchView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
    this.controller =
      services.requireService(CAGControllerType.class);
  }

  private void clearParameters()
  {

  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.accordion.setExpandedPane(this.basicParameters);

    this.idField.setDisable(true);
    this.locationField.setDisable(true);

    this.idMatch.setItems(
      FXCollections.observableArrayList(CAGItemIDMatchKind.values()));
    this.idMatch.setConverter(
      new CAGItemIDMatchConverter(this.strings));
    this.idMatch.getSelectionModel()
      .select(CAGItemIDMatchKind.ANY);
    this.idMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onIDMatchChanged(newValue);
      });

    this.locationMatch.setItems(
      FXCollections.observableArrayList(CAGLocationMatchKind.values()));
    this.locationMatch.setConverter(
      new CAGLocationMatchConverter(this.strings));
    this.locationMatch.getSelectionModel()
      .select(CAGLocationMatchKind.ANY);
    this.locationMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onLocationMatchChanged(newValue);
      });
  }

  private void onLocationMatchChanged(
    final CAGLocationMatchKind newValue)
  {
    switch (newValue) {
      case ANY -> {
        this.locationField.setDisable(true);
      }
      case EXACTLY, DESCENDANTS_OF -> {
        this.locationField.setDisable(false);
      }
    }
  }

  private void onIDMatchChanged(
    final CAGItemIDMatchKind newValue)
  {
    switch (newValue) {
      case ANY -> {
        this.idField.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO -> {
        this.idField.setDisable(false);
      }
    }
  }

  @FXML
  private void onLocationSelectSelected()
  {

  }

  @FXML
  private void onSearchSelected()
  {
    final var occurrences = new HashSet<CAStockOccurrenceKind>(2);
    if (this.includeSerial.isSelected()) {
      occurrences.add(CAStockOccurrenceKind.SERIAL);
    }
    if (this.includeSets.isSelected()) {
      occurrences.add(CAStockOccurrenceKind.SET);
    }

    this.controller.stockSearchBegin(
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        this.itemIDSelection(),
        occurrences,
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        100L
      )
    );
  }

  private CAComparisonExactType<CAItemID> itemIDSelection()
  {
    return switch (this.idMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case EQUAL_TO -> {
        yield new CAComparisonExactType.IsEqualTo<>(
          new CAItemID(UUID.fromString(this.idField.getText().trim()))
        );
      }
      case NOT_EQUAL_TO -> {
        yield new CAComparisonExactType.IsNotEqualTo<>(
          new CAItemID(UUID.fromString(this.idField.getText().trim()))
        );
      }
    };
  }

  @FXML
  private void onSearchClearSelected()
  {
    final var confirmMessage =
      this.strings.format(CARDANT_SEARCH_CONFIRMCLEAR);
    final var clearButtonMessage =
      this.strings.format(CARDANT_SEARCH_CLEAR);

    final var confirm =
      new ButtonType(clearButtonMessage, OK_DONE);
    final var cancel =
      new ButtonType(this.strings.format(CARDANT_CANCEL), CANCEL_CLOSE);

    final var dialog =
      new Alert(CONFIRMATION, confirmMessage);

    CAGCSS.setCSS(dialog.getDialogPane());

    dialog.setHeaderText(
      this.strings.format(CARDANT_SEARCH_CONFIRMCLEARTITLE));

    final var dialogButtons =
      dialog.getButtonTypes();

    dialogButtons.clear();
    dialogButtons.add(cancel);
    dialogButtons.add(confirm);

    final var r = dialog.showAndWait();
    if (r.isEmpty()) {
      return;
    }

    if (r.get().equals(confirm)) {
      this.clearParameters();
    }
  }
}
