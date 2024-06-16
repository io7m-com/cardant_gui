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
 * A location selection view.
 */

public final class CAGLocationSelectView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGLocationTreeControllerType controller;

  @FXML private Node locationTree;
  @FXML private CAGLocationTreeView locationTreeController;
  @FXML private Button select;

  /**
   * A location selection view.
   *
   * @param inStage      The stage
   * @param inController The tree controller
   */

  public CAGLocationSelectView(
    final Stage inStage,
    final CAGLocationTreeControllerType inController)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.controller =
      Objects.requireNonNull(inController, "inController");
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    this.locationTreeController.setControllers(this.controller);

    this.controller.locationSelected()
      .summary()
      .addListener((observable, oldValue, newValue) -> {
        this.select.setDisable(newValue.isEmpty());
      });
  }

  @FXML
  private void onCancelSelected()
  {
    this.controller.locationSelectNothing();
    this.stage.close();
  }

  @FXML
  private void onSelectSelected()
  {
    this.stage.close();
  }
}
