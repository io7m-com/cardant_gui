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

import com.io7m.cardant.model.CALocationSummary;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_CANCEL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOCATIONS_REMOVECONFIRM;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOCATIONS_REMOVECONFIRMTITLE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_REMOVE;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 * The table of locations.
 */

public final class CAGMainLocationTableView
  implements CAGViewType
{
  private final CAGControllerType controller;
  private final CAGStringsType strings;
  private final CAGClientServiceType clients;

  @FXML private TreeView<CALocationSummary> mainLocationTree;
  @FXML private Button locationAdd;
  @FXML private Button locationRemove;

  /**
   * The table of locations.
   *
   * @param inServices The service directory
   */

  public CAGMainLocationTableView(
    final RPServiceDirectoryType inServices)
  {
    Objects.requireNonNull(inServices, "services");

    this.controller =
      inServices.requireService(CAGControllerType.class);
    this.strings =
      inServices.requireService(CAGStringsType.class);
    this.clients =
      inServices.requireService(CAGClientServiceType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.locationRemove.setDisable(true);

    this.mainLocationTree.setCellFactory(
      new CAGLocationCellFactory(this.strings)
    );

    this.clients.status()
      .subscribe((oldStatus, newStatus) -> {
        Platform.runLater(() -> {
          this.onClientStatusChanged(oldStatus, newStatus);
        });
      });

    this.mainLocationTree.setRoot(
      this.controller.locationTree()
        .getValue()
    );

    this.controller.locationTree()
      .addListener((observable, oldValue, newValue) -> {
        this.mainLocationTree.setRoot(newValue);
      });

    this.mainLocationTree.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onLocationSelectionChanged(newValue);
      });
  }

  private void onLocationSelectionChanged(
    final TreeItem<CALocationSummary> newValue)
  {
    this.locationRemove.setDisable(true);

    if (newValue == null) {
      this.controller.locationSelectNothing();
      return;
    }

    this.locationRemove.setDisable(false);
    this.controller.locationGet(newValue.getValue().id());
  }

  private void onClientStatusChanged(
    final CAGClientStatus oldStatus,
    final CAGClientStatus newStatus)
  {
    switch (newStatus) {
      case NOT_CONNECTED, CONNECTING -> {
        // Nothing
      }
      case CONNECTED -> {
        this.controller.locationSearchBegin();
      }
    }
  }

  @FXML
  private void onLocationAddSelected()
  {

  }

  @FXML
  private void onLocationRemoveSelected()
  {
    final var confirmMessage =
      this.strings.format(CARDANT_LOCATIONS_REMOVECONFIRM);
    final var clearButtonMessage =
      this.strings.format(CARDANT_REMOVE);

    final var confirm =
      new ButtonType(clearButtonMessage, OK_DONE);
    final var cancel =
      new ButtonType(this.strings.format(CARDANT_CANCEL), CANCEL_CLOSE);

    final var dialog =
      new Alert(CONFIRMATION, confirmMessage);

    CAGCSS.setCSS(dialog.getDialogPane());

    dialog.setHeaderText(
      this.strings.format(CARDANT_LOCATIONS_REMOVECONFIRMTITLE));

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
      this.controller.locationRemove();
    }
  }
}
