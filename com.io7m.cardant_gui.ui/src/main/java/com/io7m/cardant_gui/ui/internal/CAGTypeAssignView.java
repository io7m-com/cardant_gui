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
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The type assignment view.
 */

public final class CAGTypeAssignView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGItemDetailsControllerType itemController;
  private final CAItemID item;

  @FXML private Parent root;
  @FXML private TextField packageField;
  @FXML private TextField typeField;
  @FXML private Button assignButton;
  @FXML private TextArea errorText;

  /**
   * The type assignment view.
   *
   * @param inStage   The stage
   * @param services  The services
   * @param arguments The arguments
   */

  public CAGTypeAssignView(
    final Stage inStage,
    final RPServiceDirectoryType services,
    final CAGTypeAssignDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");

    this.item =
      arguments.item();
    this.itemController =
      arguments.itemController();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.packageField.textProperty()
      .addListener(_ -> this.validate());
    this.typeField.textProperty()
      .addListener(_ -> this.validate());
  }

  private void validate()
  {
    var ok = true;
    try {
      new CATypeRecordIdentifier(
        new RDottedName(this.packageField.getText().trim()),
        new RDottedName(this.typeField.getText().trim())
      );
    } catch (final Exception e) {
      this.errorText.setText(e.getMessage());
      this.errorText.setVisible(true);
      ok = false;
    }

    if (ok) {
      this.errorText.setText("");
    }

    this.assignButton.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onAddSelected()
  {
    this.root.setDisable(true);

    final var future =
      this.itemController.itemTypeAssign(
        this.item,
        new CATypeRecordIdentifier(
          new RDottedName(this.packageField.getText().trim()),
          new RDottedName(this.typeField.getText().trim())
        )
      );

    future.whenComplete((_, exception) -> {
      Platform.runLater(() -> this.root.setDisable(false));

      if (exception == null) {
        Platform.runLater(this.stage::close);
      } else {
        Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
      }
    });
  }
}
