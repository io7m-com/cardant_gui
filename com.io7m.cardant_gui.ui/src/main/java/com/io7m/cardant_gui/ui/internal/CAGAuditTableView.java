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

import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAUserID;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The table of audit events.
 */

public final class CAGAuditTableView
  implements CAGViewType
{
  private CAGAuditControllerType controller;
  private final CAGStringsType strings;

  @FXML private TableView<CAAuditEvent> auditTable;
  @FXML private TableColumn<CAAuditEvent, Long> auditTableID;
  @FXML private TableColumn<CAAuditEvent, OffsetDateTime> auditTableTime;
  @FXML private TableColumn<CAAuditEvent, CAUserID> auditTableOwner;
  @FXML private TableColumn<CAAuditEvent, String> auditTableType;
  @FXML private TableColumn<CAAuditEvent, Map<String, String>> auditTableData;
  @FXML private Label resultsLabel;
  @FXML private Button pageNext;
  @FXML private Button pagePrevious;

  /**
   * The table of audit events.
   *
   * @param inServices The service directory
   */

  public CAGAuditTableView(
    final RPServiceDirectoryType inServices)
  {
    Objects.requireNonNull(inServices, "services");

    this.strings =
      inServices.requireService(CAGStringsType.class);
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

    this.controller.auditEventsViewSorted()
      .comparatorProperty()
      .bind(this.auditTable.comparatorProperty());

    this.auditTable.setItems(
      this.controller.auditEventsViewSorted());

    this.controller.auditEventsView()
      .addListener(this::onAuditEventViewChanged);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.auditTable.setPlaceholder(new Label(""));

    this.auditTableID.setSortable(true);
    this.auditTableID.setReorderable(false);
    this.auditTableID.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(
        Long.valueOf(param.getValue().id())
      );
    });

    this.auditTableTime.setSortable(true);
    this.auditTableTime.setReorderable(false);
    this.auditTableTime.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().time());
    });

    this.auditTableOwner.setSortable(true);
    this.auditTableOwner.setReorderable(false);
    this.auditTableOwner.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().owner());
    });

    this.auditTableType.setSortable(true);
    this.auditTableType.setReorderable(false);
    this.auditTableType.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().type());
    });

    this.auditTableData.setSortable(true);
    this.auditTableData.setReorderable(false);
    this.auditTableData.setCellFactory(new CAGAuditDataCellFactory(this.strings));
    this.auditTableData.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().data());
    });
  }

  private void onAuditEventViewChanged(
    final ListChangeListener.Change<? extends CAAuditEvent> c)
  {
    final var size = c.getList().size();
    if (size > 0) {
      final var range =
        this.controller.auditEventsPages().getValue();

      this.resultsLabel.setText(
        this.strings.format(
          CAGStringConstants.CARDANT_AUDITSEARCH_PAGEOF,
          Long.valueOf(range.pageIndex()),
          Long.valueOf(range.pageCount())
        )
      );

      this.pagePrevious
        .setDisable(!range.hasPrevious());
      this.pageNext
        .setDisable(!range.hasNext());
    } else {
      this.resultsLabel.setText("");
    }
  }

  @FXML
  private void onPagePreviousSelected()
  {
    this.controller.auditSearchPrevious();
  }

  @FXML
  private void onPageNextSelected()
  {
    this.controller.auditSearchNext();
  }
}
