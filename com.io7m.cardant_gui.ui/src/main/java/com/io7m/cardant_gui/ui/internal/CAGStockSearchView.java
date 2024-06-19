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
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CALocationMatchType.CALocationExact;
import com.io7m.cardant.model.CALocationMatchType.CALocationWithDescendants;
import com.io7m.cardant.model.CALocationMatchType.CALocationsAll;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
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
 * A stock search view.
 */

public final class CAGStockSearchView
  extends CAGAbstractResourceHolder
  implements CAGViewType
{
  private final CAGStringsType strings;
  private final CAGLocationSelectDialogs locationSelectDialogs;
  private final CAGClientServiceType client;
  private final CAGItemSelectDialogs itemSelectDialogs;
  private final CAGEventServiceType events;

  @FXML private ChoiceBox<CAGLocationMatchKind> locationMatch;
  @FXML private TextField locationField;
  @FXML private ChoiceBox<CAGItemIDMatchKind> itemMatch;
  @FXML private TextField itemField;
  @FXML private CheckBox includeSerial;
  @FXML private CheckBox includeSets;
  @FXML private TitledPane basicParameters;
  @FXML private Accordion accordion;
  @FXML private Button locationSelect;
  @FXML private Button itemSelect;

  private CAGStockSearchControllerType controller;
  private CAGItemSearchControllerType searchController;
  private CAGItemDetailsControllerType detailsController;

  /**
   * A stock search view.
   *
   * @param services The service directory
   */

  public CAGStockSearchView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
    this.client =
      services.requireService(CAGClientServiceType.class);
    this.locationSelectDialogs =
      services.requireService(CAGLocationSelectDialogs.class);
    this.itemSelectDialogs =
      services.requireService(CAGItemSelectDialogs.class);
    this.events =
      services.requireService(CAGEventServiceType.class);
  }

  /**
   * Set the controllers.
   *
   * @param inController The controller
   */

  public void setControllers(
    final CAGStockSearchControllerType inController)
  {
    this.controller =
      Objects.requireNonNull(inController, "controller");

    this.searchController =
      this.trackResource(
        CAGItemSearchController.create(this.events, this.client)
      );
    this.detailsController =
      this.trackResource(
        CAGItemDetailsController.create(this.events, this.client)
      );
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

    this.itemField.setDisable(true);
    this.locationField.setDisable(true);

    this.itemMatch.setItems(
      FXCollections.observableArrayList(CAGItemIDMatchKind.values()));
    this.itemMatch.setConverter(
      new CAGItemIDMatchConverter(this.strings));
    this.itemMatch.getSelectionModel()
      .select(CAGItemIDMatchKind.ANY);
    this.itemMatch.getSelectionModel()
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
        this.locationSelect.setDisable(true);
      }
      case EXACTLY, DESCENDANTS_OF -> {
        this.locationField.setDisable(false);
        this.locationSelect.setDisable(false);
      }
    }
  }

  private void onIDMatchChanged(
    final CAGItemIDMatchKind newValue)
  {
    switch (newValue) {
      case ANY -> {
        this.itemField.setDisable(true);
        this.itemSelect.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO -> {
        this.itemField.setDisable(false);
        this.itemSelect.setDisable(false);
      }
    }
  }

  @FXML
  private void onItemSelectSelected()
    throws IOException
  {
    this.itemSelectDialogs.openDialogAndWait(
      new CAGItemSelectDialogArguments(
        this.detailsController,
        this.searchController
      )
    );

    final var selectedLocationOpt =
      this.detailsController.itemSelected()
        .summary()
        .getValue();

    selectedLocationOpt.ifPresent(item -> {
      this.itemField.setText(item.id().displayId());
    });
  }

  @FXML
  private void onLocationSelectSelected()
    throws IOException
  {
    final var locationController =
      CAGLocationTreeController.create(this.client);

    this.locationSelectDialogs.openDialogAndWait(locationController);

    final var selectedLocationOpt =
      locationController.locationSelected()
        .summary()
        .getValue();

    selectedLocationOpt.ifPresent(location -> {
      this.locationField.setText(location.id().displayId());
    });
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
        this.locationSelection(),
        this.itemIDSelection(),
        occurrences,
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        100L
      )
    );
  }

  private CALocationMatchType locationSelection()
  {
    return switch (this.locationMatch.getValue()) {
      case ANY -> {
        yield new CALocationsAll();
      }
      case EXACTLY -> {
        yield new CALocationExact(
          CALocationID.of(this.locationField.getText().trim())
        );
      }
      case DESCENDANTS_OF -> {
        yield new CALocationWithDescendants(
          CALocationID.of(this.locationField.getText().trim())
        );
      }
    };
  }

  private CAComparisonExactType<CAItemID> itemIDSelection()
  {
    return switch (this.itemMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case EQUAL_TO -> {
        yield new CAComparisonExactType.IsEqualTo<>(
          new CAItemID(UUID.fromString(this.itemField.getText().trim()))
        );
      }
      case NOT_EQUAL_TO -> {
        yield new CAComparisonExactType.IsNotEqualTo<>(
          new CAItemID(UUID.fromString(this.itemField.getText().trim()))
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
