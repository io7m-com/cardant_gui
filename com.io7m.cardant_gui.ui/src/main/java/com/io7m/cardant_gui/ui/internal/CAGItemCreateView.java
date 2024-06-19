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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The item creation view.
 */

public final class CAGItemCreateView implements CAGViewType
{
  private final CAGItemDetailsControllerType controller;
  private final Stage stage;

  @FXML private Button create;
  @FXML private Button cancel;
  @FXML private Button generate;
  @FXML private TextField itemId;
  @FXML private TextField itemName;

  private CAItemID itemIdSelected;
  private String itemNameSelected;

  /**
   * The item creation view.
   *
   * @param inController The controller
   * @param inStage The stage
   */

  public CAGItemCreateView(
    final CAGItemDetailsControllerType inController,
    final Stage inStage)
  {
    this.controller =
      Objects.requireNonNull(inController, "controller");
    this.stage =
      Objects.requireNonNull(inStage, "stage");
  }

  @FXML
  private void onGenerateSelected()
  {
    this.itemId.setText(CAItemID.random().displayId());
  }

  @FXML
  private void onCreateSelected()
  {
    this.controller.itemCreate(this.itemIdSelected, this.itemNameSelected);
    this.stage.close();
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    this.itemId.textProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.validate();
      });

    this.itemName.textProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.validate();
      });
  }

  private void validate()
  {
    this.create.setDisable(true);

    try {
      this.itemIdSelected = CAItemID.of(this.itemId.getText());
    } catch (final Exception e) {
      return;
    }

    this.itemNameSelected = this.itemName.getText();
    if (this.itemNameSelected.isBlank()) {
      return;
    }

    this.create.setDisable(false);
  }
}
