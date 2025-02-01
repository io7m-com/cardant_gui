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
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCKSEARCH_CONFIRMDELETE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCKSEARCH_CONFIRMDELETESERIAL;

/**
 * The table of stock.
 */

public final class CAGStockTableView
  implements CAGViewType
{
  private final CAGStockSetMoveDialogs stockSetMoveDialogs;
  private final CAGClientServiceType client;
  private final CAGEventServiceType events;
  private final CAGLocationSelectDialogs locationDialogs;
  private final CAGStockAddDialogs stockAddDialogs;
  private final CAGStockSerialAddDialogs stockSerialAddDialogs;
  private final CAGStringsType strings;
  private final MenuItem contextItemOpen;
  private final MenuItem contextLocationOpen;
  private final ObservableList<CAItemSerial> serials;
  private final RPServiceDirectoryType services;
  private CAGStockSearchControllerType controller;

  @FXML private final ContextMenu contextMenu;
  @FXML private Button serialAdd;
  @FXML private Button serialRemove;
  @FXML private Button stockAdd;
  @FXML private Button stockMove;
  @FXML private Button stockRemove;
  @FXML private Label resultsLabel;
  @FXML private ListView<CAItemSerial> serialList;
  @FXML private Pane stockDetails;
  @FXML private TableColumn<CAStockOccurrenceType, CAItemID> colItem;
  @FXML private TableColumn<CAStockOccurrenceType, Long> colCount;
  @FXML private TableColumn<CAStockOccurrenceType, String> colLocation;
  @FXML private TableColumn<CAStockOccurrenceType, String> colName;
  @FXML private TableColumn<CAStockOccurrenceType, String> colSerial;
  @FXML private TableView<CAStockOccurrenceType> stockTable;
  @FXML private TextField instanceField;

  /**
   * The table of stock.
   *
   * @param inServices The service directory
   */

  public CAGStockTableView(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");

    this.strings =
      inServices.requireService(CAGStringsType.class);
    this.events =
      inServices.requireService(CAGEventServiceType.class);
    this.locationDialogs =
      inServices.requireService(CAGLocationSelectDialogs.class);
    this.stockAddDialogs =
      inServices.requireService(CAGStockAddDialogs.class);
    this.stockSerialAddDialogs =
      inServices.requireService(CAGStockSerialAddDialogs.class);
    this.stockSetMoveDialogs =
      inServices.requireService(CAGStockSetMoveDialogs.class);
    this.client =
      inServices.requireService(CAGClientServiceType.class);

    this.contextLocationOpen = new MenuItem("Open in location view…");
    this.contextLocationOpen.setOnAction(_ -> this.onRequestLocationOpen());

    this.contextItemOpen = new MenuItem("Open in item view…");
    this.contextItemOpen.setOnAction(_ -> this.onRequestItemOpen());

    this.contextMenu = new ContextMenu();
    this.contextMenu.getItems()
      .setAll(
        List.of(
          this.contextLocationOpen,
          this.contextItemOpen
        )
      );

    this.serials = FXCollections.observableArrayList();
  }

  private static Long stockCount(
    final CAStockOccurrenceType value)
  {
    return switch (value) {
      case final CAStockOccurrenceSerial _ -> Long.valueOf(1L);
      case final CAStockOccurrenceSet set -> Long.valueOf(set.count());
    };
  }

  private static String stockSerialText(
    final CAStockOccurrenceType value)
  {
    return switch (value) {
      case final CAStockOccurrenceSerial serial -> {
        final var serials = serial.serials();
        if (serials.isEmpty()) {
          yield "";
        }
        final var text = new StringBuilder();
        text.append(serials.getFirst().value());
        if (serials.size() > 1) {
          text.append(", …");
        }
        yield text.toString();
      }
      case final CAStockOccurrenceSet _ -> "";
    };
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

    this.controller.stockView()
      .addListener(this::onStocksViewChanged);

    this.stockTable.setItems(
      this.controller.stockViewSorted());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.stockDetails.setDisable(true);
    this.stockMove.setDisable(true);
    this.stockRemove.setDisable(true);

    this.serialList.setItems(this.serials);
    this.serialList.getSelectionModel()
      .selectedItemProperty()
      .addListener((_, _, newValue) -> {
        this.onSerialSelectionChanged(newValue);
      });

    this.serialRemove.setDisable(true);

    this.stockTable.setPlaceholder(new Label(""));
    this.stockTable.getSelectionModel()
      .selectedItemProperty()
      .addListener((_, _, newValue) -> this.onTableSelectionChanged(newValue));

    this.colLocation.setReorderable(false);
    this.colLocation.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(
        param.getValue().location().path().toString()
      );
    });

    this.colItem.setReorderable(false);
    this.colItem.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(param.getValue().item().id());
    });

    this.colName.setReorderable(false);
    this.colName.setCellValueFactory(param -> {
      return new ReadOnlyStringWrapper(param.getValue().item().name());
    });

    this.colSerial.setReorderable(false);
    this.colSerial.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(stockSerialText(param.getValue()));
    });

    this.colCount.setReorderable(false);
    this.colCount.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(stockCount(param.getValue()));
    });
  }

  private void onSerialSelectionChanged(
    final CAItemSerial newValue)
  {
    if (newValue == null) {
      this.serialRemove.setDisable(true);
      return;
    }

    this.serialRemove.setDisable(false);
  }

  private void onTableSelectionChanged(
    final CAStockOccurrenceType occurrence)
  {
    if (occurrence == null) {
      this.stockTable.setContextMenu(null);
      this.stockDetails.setDisable(true);
      this.stockRemove.setDisable(true);
      this.stockMove.setDisable(true);
      this.serials.clear();
      this.instanceField.setText("");
      this.controller.stockSelectNone();
      return;
    }

    this.stockTable.setContextMenu(this.contextMenu);
    this.stockDetails.setDisable(false);
    this.stockMove.setDisable(false);
    this.stockRemove.setDisable(false);
    this.instanceField.setText(occurrence.instance().displayId());
    this.controller.stockSelect(occurrence);

    switch (occurrence) {
      case final CAStockOccurrenceSerial serial -> {
        this.serials.setAll(serial.serials());
        this.serialAdd.setDisable(false);
      }
      case final CAStockOccurrenceSet _ -> {
        this.serialRemove.setDisable(true);
        this.serialAdd.setDisable(true);
        this.serials.clear();
      }
    }
  }

  private void onStocksViewChanged(
    final Observable observable)
  {

  }

  @FXML
  private void onPagePreviousSelected()
  {

  }

  @FXML
  private void onPageNextSelected()
  {

  }

  @FXML
  private void onStockAddSelected()
    throws IOException
  {
    final var locationController =
      CAGLocationTreeController.create(this.client);
    final var itemSearchController =
      CAGItemSearchController.create(this.events, this.client);
    final var itemDetailsController =
      CAGItemDetailsController.create(this.events, this.client);

    this.stockAddDialogs.openDialogAndWait(
      new CAGStockAddDialogArguments(
        this.controller,
        locationController,
        itemDetailsController,
        itemSearchController
      )
    );
  }

  @FXML
  private void onStockMoveSelected()
    throws IOException
  {
    final var existing =
      this.stockTable.getSelectionModel()
        .getSelectedItem();

    switch (existing) {
      case final CAStockOccurrenceSerial serial -> {
        final var locationController =
          CAGLocationTreeController.create(this.client);

        this.locationDialogs.openDialogAndWait(locationController);

        final var locationSelected =
          locationController.locationSelected();
        final var summaryOpt =
          locationSelected.summary().getValue();

        if (summaryOpt.isEmpty()) {
          return;
        }

        this.controller.stockSerialMove(serial, summaryOpt.get().id());
      }

      case final CAStockOccurrenceSet set -> {
        this.stockSetMoveDialogs.openDialogAndWait(
          new CAGStockSetMoveDialogArguments(
            this.services,
            set,
            this.controller
          )
        );
      }
    }
  }

  @FXML
  private void onStockRemoveSelected()
  {
    final var existing =
      this.stockTable.getSelectionModel()
        .getSelectedItem();

    final var alert =
      new Alert(
        Alert.AlertType.CONFIRMATION,
        this.strings.format(CARDANT_STOCKSEARCH_CONFIRMDELETE)
      );

    final var r = alert.showAndWait();
    if (r.isEmpty()) {
      return;
    }

    final var button = r.get();
    if (button.equals(ButtonType.OK)) {
      this.controller.stockRemoveAll(existing.instance());
    }
  }

  @FXML
  private void onSerialAddSelected()
    throws IOException
  {
    final var existing =
      this.stockTable.getSelectionModel()
        .getSelectedItem();

    this.stockSerialAddDialogs.openDialogAndWait(
      new CAGStockSerialAddDialogArguments(
        existing.instance(),
        this.controller
      )
    );
  }

  @FXML
  private void onSerialRemoveSelected()
  {
    final var instanceExisting =
      this.stockTable.getSelectionModel()
        .getSelectedItem();

    final var serialExisting =
      this.serialList.getSelectionModel()
        .getSelectedItem();

    final var alert =
      new Alert(
        Alert.AlertType.CONFIRMATION,
        this.strings.format(CARDANT_STOCKSEARCH_CONFIRMDELETESERIAL)
      );

    CAGCSS.setCSS(alert.getDialogPane());
    final var r = alert.showAndWait();
    if (r.isPresent()) {
      final var rr = r.get();
      if (rr.equals(ButtonType.OK)) {
        this.controller.stockSerialRemove(
          instanceExisting.instance(),
          serialExisting
        );
      }
    }
  }

  private void onRequestItemOpen()
  {

  }

  private void onRequestLocationOpen()
  {

  }
}
