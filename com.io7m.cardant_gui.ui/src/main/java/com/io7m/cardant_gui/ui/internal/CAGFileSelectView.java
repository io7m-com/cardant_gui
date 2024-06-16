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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A file selection view.
 */

public final class CAGFileSelectView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGFileSearchControllerType controller;

  @FXML private Node fileList;
  @FXML private CAGFileListView fileListController;
  @FXML private Node fileSearch;
  @FXML private CAGFileSearchView fileSearchController;
  @FXML private Button select;

  /**
   * A file selection view.
   *
   * @param inStage      The stage
   * @param inController The tree controller
   */

  public CAGFileSelectView(
    final Stage inStage,
    final CAGFileSearchControllerType inController)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.controller =
      Objects.requireNonNull(inController, "inController");
  }

  @Override
  public void initialize(
    final URL file,
    final ResourceBundle resources)
  {
    this.fileListController
      .setControllers(this.controller);
    this.fileSearchController
      .setControllers(this.controller);

    this.controller.fileSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.select.setDisable(newValue.isEmpty());
      });
  }

  @FXML
  private void onCancelSelected()
  {
    this.controller.fileSelectNone();
    this.stage.close();
  }

  @FXML
  private void onSelectSelected()
  {
    this.stage.close();
  }
}
