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
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * The attachment addition view.
 */

public final class CAGAttachmentAddView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStringsType strings;
  private final CAGControllerType controller;

  @FXML private Button addButton;
  @FXML private Label resultsLabel;
  @FXML private TextField itemField;
  @FXML private TextField fileField;
  @FXML private TextField relationField;
  @FXML private ListView<CAFileType.CAFileWithoutData> files;

  /**
   * The attachment addition view.
   *
   * @param inStage  The stage
   * @param services The services
   */

  public CAGAttachmentAddView(
    final Stage inStage,
    final RPServiceDirectoryType services)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.strings =
      services.requireService(CAGStringsType.class);
    this.controller =
      services.requireService(CAGControllerType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.addButton.setDisable(true);

    this.files.setCellFactory(
      new CAGFileCellFactory(this.strings));
    this.files.setItems(
      this.controller.filesView());
    this.files.getSelectionModel()
      .setSelectionMode(SelectionMode.SINGLE);
    this.files.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CAFileType.CAFileWithoutData>) this::onFileSelectionChanged);

    this.controller.filesView()
      .addListener(this::onFilesViewChanged);

    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemSelectionChanged(newValue);
      });

    this.onItemSelectionChanged(
      this.controller.itemSelected().getValue()
    );

    this.itemField.textProperty()
      .addListener(observable -> {
        this.validate();
      });

    this.fileField.textProperty()
      .addListener(observable -> {
        this.validate();
      });

    this.relationField.textProperty()
      .addListener(observable -> {
        this.validate();
      });
  }

  private void validate()
  {
    this.addButton.setDisable(true);

    try {
      this.createCommand();
      this.addButton.setDisable(false);
    } catch (final Exception e) {
      // Ignored
      this.addButton.setDisable(true);
    }
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
      throw new IllegalArgumentException();
    }

    return new CAICommandItemAttachmentAdd(
      new CAItemID(UUID.fromString(itemIdText)),
      new CAFileID(UUID.fromString(fileIdText)),
      relationText
    );
  }

  private void onItemSelectionChanged(
    final CAItemSummary item)
  {
    if (item == null) {
      this.itemField.setText("");
      return;
    }

    this.itemField.setText(item.id().displayId());
  }

  private void onFilesViewChanged(
    final ListChangeListener.Change<? extends CAFileType.CAFileWithoutData> c)
  {
    final var size = c.getList().size();
    if (size > 0) {
      final var range =
        this.controller.filePages().getValue();

      this.resultsLabel.setText(
        this.strings.format(
          CAGStringConstants.CARDANT_FILESEARCH_PAGEOF,
          Long.valueOf(range.pageIndex()),
          Long.valueOf(range.pageCount())
        )
      );
    } else {
      this.resultsLabel.setText("");
    }
  }

  private void onFileSelectionChanged(
    final ListChangeListener.Change<? extends CAFileType.CAFileWithoutData> c)
  {
    final var selected = c.getList();
    if (selected.isEmpty()) {
      return;
    }

    this.fileField.setText(selected.get(0).id().id().toString());
  }

  @FXML
  private void onAddSelected()
  {
    this.controller.itemAttachmentAdd(this.createCommand());
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onPageNextSelected()
  {

  }

  @FXML
  private void onPagePreviousSelected()
  {

  }
}
