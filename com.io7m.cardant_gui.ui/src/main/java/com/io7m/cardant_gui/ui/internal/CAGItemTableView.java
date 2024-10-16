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
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_CANCEL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_DELETE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMDELETE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMDELETE_TITLE;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 * The table of items.
 */

public final class CAGItemTableView
  implements CAGViewType
{
  private final CAGStringsType strings;
  private final CAGItemCreateDialogs createDialogs;
  private CAGItemSearchControllerType search;
  private CAGItemDetailsControllerType details;

  @FXML private TableView<CAItemSummary> mainItemTable;
  @FXML private TableColumn<CAItemSummary, String> colId;
  @FXML private TableColumn<CAItemSummary, String> colName;
  @FXML private Label resultsLabel;
  @FXML private Button itemAdd;
  @FXML private Button itemRemove;

  /**
   * The table of items.
   *
   * @param inServices The service directory
   */

  public CAGItemTableView(
    final RPServiceDirectoryType inServices)
  {
    Objects.requireNonNull(inServices, "services");

    this.strings =
      inServices.requireService(CAGStringsType.class);
    this.createDialogs =
      inServices.requireService(CAGItemCreateDialogs.class);
  }

  /**
   * Set the controlers.
   *
   * @param inSearch  The search controller
   * @param inDetails The details controller
   */

  public void setControllers(
    final CAGItemSearchControllerType inSearch,
    final CAGItemDetailsControllerType inDetails)
  {
    this.search =
      Objects.requireNonNull(inSearch, "search");
    this.details =
      Objects.requireNonNull(inDetails, "details");

    this.search.itemsViewSorted()
      .comparatorProperty()
      .bind(this.mainItemTable.comparatorProperty());

    this.search.itemsView()
      .addListener(this::onItemsViewChanged);

    this.mainItemTable.setItems(
      this.search.itemsViewSorted());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.mainItemTable.setPlaceholder(new Label(""));

    this.colId.setSortable(true);
    this.colId.setReorderable(false);
    this.colId.setCellValueFactory(
      param -> {
        return new ReadOnlyStringWrapper(param.getValue().id().displayId());
      });

    this.colName.setSortable(true);
    this.colName.setReorderable(false);
    this.colName.setCellValueFactory(
      param -> {
        return new ReadOnlyStringWrapper(param.getValue().name());
      });

    this.mainItemTable.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CAItemSummary>) this::onItemSelectionChanged);
  }

  private void onItemSelectionChanged(
    final ListChangeListener.Change<? extends CAItemSummary> c)
  {
    this.itemRemove.setDisable(true);

    final var selected = c.getList();
    if (selected.isEmpty()) {
      this.details.itemSelectNothing();
      return;
    }

    if (selected.size() == 1) {
      final var selectedItem = selected.get(0);
      this.itemRemove.setDisable(false);
      this.details.itemSelect(selectedItem.id());
      return;
    }
  }

  private void onItemsViewChanged(
    final ListChangeListener.Change<? extends CAItemSummary> c)
  {
    final var size = c.getList().size();
    if (size > 0) {
      final var range =
        this.search.itemPages().getValue();

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
    throws IOException
  {
    this.createDialogs.openDialogAndWait(this.details);
  }

  @FXML
  private void onItemRemoveSelected()
  {
    final var confirmMessage =
      this.strings.format(CARDANT_ITEMDELETE);
    final var deleteButtonMessage =
      this.strings.format(CARDANT_DELETE);

    final var confirm =
      new ButtonType(deleteButtonMessage, OK_DONE);
    final var cancel =
      new ButtonType(this.strings.format(CARDANT_CANCEL), CANCEL_CLOSE);

    final var dialog =
      new Alert(CONFIRMATION, confirmMessage);

    CAGCSS.setCSS(dialog.getDialogPane());

    dialog.setHeaderText(
      this.strings.format(CARDANT_ITEMDELETE_TITLE));

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
      this.details.itemDelete(
        this.details.itemSelected()
          .summary()
          .getValue()
          .orElseThrow()
          .id()
      );
    }
  }
}
