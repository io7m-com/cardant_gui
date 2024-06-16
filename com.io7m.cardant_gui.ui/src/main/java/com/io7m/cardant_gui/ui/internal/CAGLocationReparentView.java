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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The location reparent view.
 */

public final class CAGLocationReparentView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStringsType strings;
  private final CAGLocationTreeControllerType controller;
  private final CALocationID locationID;

  @FXML private TreeView<CALocationSummary> locationTree;
  @FXML private Button cancel;
  @FXML private Button reparent;

  /**
   * The location reparent view.
   *
   * @param inStage      The stage
   * @param services     The services
   * @param inLocationID The child location
   * @param inController The tree controller
   */

  public CAGLocationReparentView(
    final Stage inStage,
    final RPServiceDirectoryType services,
    final CALocationID inLocationID,
    final CAGLocationTreeControllerType inController)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.strings =
      services.requireService(CAGStringsType.class);
    this.locationID =
      Objects.requireNonNull(inLocationID, "locationID");
    this.controller =
      Objects.requireNonNull(inController, "controller");
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.locationTree.setCellFactory(
      new CAGLocationCellFactory(this.strings)
    );

    this.locationTree.setRoot(
      this.controller.locationTree()
        .getValue()
    );

    this.controller.locationTree()
      .addListener((observable, oldValue, newValue) -> {
        this.locationTree.setRoot(newValue);
      });

    this.locationTree.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onTreeSelectionChanged(newValue);
      });
  }

  private void onTreeSelectionChanged(
    final TreeItem<CALocationSummary> newValue)
  {
    this.reparent.setDisable(true);

    if (newValue != null) {
      this.reparent.setDisable(false);
    }
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onReparentSelected()
  {
    final var newParent =
      this.locationTree.getSelectionModel()
        .getSelectedItem()
        .getValue()
        .id();

    this.controller.locationReparent(this.locationID, newParent);
    this.stage.close();
  }
}
