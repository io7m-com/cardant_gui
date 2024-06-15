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
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The table of stock.
 */

public final class CAGMainStockTableView
  implements CAGViewType
{
  private final CAGControllerType controller;
  private final CAGStringsType strings;

  @FXML private TableView<CAStockOccurrenceType> mainStockTable;
  @FXML private TableColumn<CAStockOccurrenceType, CALocationSummary> colLocation;
  @FXML private TableColumn<CAStockOccurrenceType, CAItemID> colItem;
  @FXML private TableColumn<CAStockOccurrenceType, String> colName;
  @FXML private TableColumn<CAStockOccurrenceType, CAStockOccurrenceType> colSerial;
  @FXML private TableColumn<CAStockOccurrenceType, CAStockOccurrenceType> colCount;
  @FXML private Label resultsLabel;
  @FXML private Button itemAdd;
  @FXML private Button itemRemove;

  /**
   * The table of stock.
   *
   * @param inServices The service directory
   */

  public CAGMainStockTableView(
    final RPServiceDirectoryType inServices)
  {
    Objects.requireNonNull(inServices, "services");

    this.controller =
      inServices.requireService(CAGControllerType.class);
    this.strings =
      inServices.requireService(CAGStringsType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.mainStockTable.setPlaceholder(new Label(""));
    this.mainStockTable.setSelectionModel(null);

    this.colLocation.setReorderable(false);
    this.colLocation.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(param.getValue().location());
    });
    this.colLocation.setCellFactory(param -> {
      return new CAGStockTableLocationCell(this.controller, this.strings);
    });

    this.colItem.setReorderable(false);
    this.colItem.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(param.getValue().item().id());
    });
    this.colItem.setCellFactory(param -> {
      return new CAGStockTableItemCell(this.controller, this.strings);
    });

    this.colName.setReorderable(false);
    this.colName.setCellValueFactory(param -> {
      return new ReadOnlyStringWrapper(param.getValue().item().name());
    });
    this.colName.setCellFactory(param -> {
      return new CAGStockTableNameCell();
    });

    this.colSerial.setReorderable(false);
    this.colSerial.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    });
    this.colSerial.setCellFactory(param -> {
      return new CAGStockTableSerialCell();
    });

    this.colCount.setReorderable(false);
    this.colCount.setCellValueFactory(param -> {
      return new ReadOnlyObjectWrapper<>(param.getValue());
    });
    this.colCount.setCellFactory(param -> {
      return new CAGStockTableCountCell();
    });

    this.controller.stockView()
      .addListener(this::onStocksViewChanged);

    this.mainStockTable.setItems(this.controller.stockViewSorted());
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
  private void onItemAddSelected()
  {

  }

  @FXML
  private void onItemRemoveSelected()
  {

  }
}
