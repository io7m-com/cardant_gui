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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ERROR_RELATIONEMPTY;

/**
 * The attachment addition view.
 */

public final class CAGItemAttachmentAddView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStringsType strings;
  private final CAGItemDetailsControllerType itemController;
  private final CAItemID itemId;
  private final CAGFileSelectDialogs fileSelectDialogs;
  private final CAGClientServiceType client;
  private final CAGFileSearchControllerType fileSearchController;

  @FXML private Button addButton;
  @FXML private TextField itemField;
  @FXML private TextField fileField;
  @FXML private TextField relationField;

  /**
   * The attachment addition view.
   *
   * @param inStage   The stage
   * @param services  The services
   * @param arguments The arguments
   */

  public CAGItemAttachmentAddView(
    final Stage inStage,
    final RPServiceDirectoryType services,
    final CAGItemAttachmentAddDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.strings =
      services.requireService(CAGStringsType.class);
    this.fileSelectDialogs =
      services.requireService(CAGFileSelectDialogs.class);
    this.client =
      services.requireService(CAGClientServiceType.class);

    this.fileSearchController =
      CAGFileSearchController.create(this.client);
    this.itemController =
      arguments.detailsController();
    this.itemId =
      arguments.item();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.itemField.setText(this.itemId.displayId());

    this.fileSearchController.fileSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.addButton.setDisable(newValue.isEmpty());
      });

    this.fileSearchController.fileSelected()
      .addListener((observable, oldValue, newValue) -> {
        if (newValue.isPresent()) {
          this.fileField.setText(newValue.get().id().displayId());
        } else {
          this.fileField.setText("");
        }
      });
  }

  private CAICommandItemAttachmentAdd createCommand()
  {
    final var relationText =
      this.relationField.getText().trim();
    final var itemIdText =
      this.itemField.getText().trim();
    final var fileIdText =
      this.fileField.getText().trim();

    if (relationText.isEmpty()) {
      final var alert =
        new Alert(
        Alert.AlertType.ERROR,
        this.strings.format(CARDANT_ERROR_RELATIONEMPTY)
      );

      alert.showAndWait();
      throw new IllegalArgumentException();
    }

    return new CAICommandItemAttachmentAdd(
      new CAItemID(UUID.fromString(itemIdText)),
      new CAFileID(UUID.fromString(fileIdText)),
      relationText
    );
  }

  @FXML
  private void onAddSelected()
  {
    this.itemController.itemAttachmentAdd(this.createCommand());
    this.stage.close();
  }

  @FXML
  private void onFileSelectSelected()
    throws IOException
  {
    this.fileSelectDialogs.openDialogAndWait(this.fileSearchController);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }
}
