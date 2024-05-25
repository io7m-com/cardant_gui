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

import com.io7m.cardant.model.CAItemSummary;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * The table of items.
 */

public final class CAGMainItemTableView
  implements CAGViewType
{
  private final CAGControllerType controller;
  private final CAGStringsType strings;

  @FXML private TableView<CAItemSummary> mainItemTable;
  @FXML private Label resultsLabel;
  @FXML private Button itemAdd;
  @FXML private Button itemRemove;

  /**
   * The table of items.
   *
   * @param inServices The service directory
   */

  public CAGMainItemTableView(
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

    this.mainItemTable.setPlaceholder(new Label(""));

    final var tableColumns =
      this.mainItemTable.getColumns();

    final var tableIDColumn =
      (TableColumn<CAItemSummary, UUID>) tableColumns.get(0);
    final var tableNameColumn =
      (TableColumn<CAItemSummary, String>) tableColumns.get(1);

    tableIDColumn.setSortable(true);
    tableIDColumn.setReorderable(false);
    tableIDColumn.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(param.getValue().id().id());
      });

    tableNameColumn.setSortable(true);
    tableNameColumn.setReorderable(false);
    tableNameColumn.setCellValueFactory(
      param -> {
        return new ReadOnlyStringWrapper(param.getValue().name());
      });

    this.controller.itemsViewSorted()
      .comparatorProperty()
      .bind(this.mainItemTable.comparatorProperty());

    this.controller.itemsView()
      .addListener(this::onItemsViewChanged);

    this.mainItemTable.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CAItemSummary>) this::onItemSelectionChanged);

    this.mainItemTable.setItems(this.controller.itemsViewSorted());
  }

  private void onItemSelectionChanged(
    final ListChangeListener.Change<? extends CAItemSummary> c)
  {
    this.itemRemove.setDisable(true);

    final var selected = c.getList();
    if (selected.isEmpty()) {
      this.controller.itemSelectNothing();
      return;
    }

    if (selected.size() == 1) {
      final var selectedItem = selected.get(0);
      this.itemRemove.setDisable(false);
      this.controller.itemGet(selectedItem.id());
      return;
    }
  }

  private void onItemsViewChanged(
    final ListChangeListener.Change<? extends CAItemSummary> c)
  {
    final var size = c.getList().size();
    if (size > 0) {
      final var range =
        this.controller.itemPages().getValue();

      this.resultsLabel.setText(
        this.strings.format(
          CAGStringConstants.CARDANT_ITEMSEARCH_PAGEOF,
          Long.valueOf(range.pageIndex()),
          Long.valueOf(range.pageCount())
        )
      );
    } else {
      this.resultsLabel.setText("");
    }
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
