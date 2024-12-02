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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * An item name setting view.
 */

public final class CAGItemNameSetView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGItemDetailsControllerType details;
  private final CAItemID itemId;
  private final String oldName;

  @FXML private Parent root;
  @FXML private TextField itemField;
  @FXML private TextField oldNameField;
  @FXML private TextField newNameField;
  @FXML private Button setButton;

  /**
   * An item name setting view.
   *
   * @param inStage   The stage
   * @param arguments The arguments
   */

  public CAGItemNameSetView(
    final Stage inStage,
    final CAGItemNameSetDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.details =
      arguments.detailsController();
    this.itemId =
      arguments.item();
    this.oldName =
      arguments.oldName();
  }

  @Override
  public void initialize(
    final URL item,
    final ResourceBundle resources)
  {
    this.itemField.setText(this.itemId.displayId());
    this.oldNameField.setText(this.oldName);
    this.newNameField.setText(this.oldName);

    this.newNameField.textProperty()
      .addListener(_ -> this.validate());
  }

  private void validate()
  {
    var ok = true;

    final var newText =
      this.newNameField.getText().trim();

    ok = ok && !newText.isBlank();
    ok = ok && !newText.equals(this.oldName);

    this.setButton.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onSetSelected()
  {
    this.root.setDisable(true);

    this.details.itemSetName(
      this.itemId,
      this.newNameField.getText().trim()
    ).whenComplete((_, exception) -> {
      Platform.runLater(() -> this.root.setDisable(false));

      if (exception == null) {
        Platform.runLater(this.stage::close);
      }
    });
  }
}
