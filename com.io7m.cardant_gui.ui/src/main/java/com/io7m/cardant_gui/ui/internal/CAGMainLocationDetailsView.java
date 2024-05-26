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

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentRelations;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The main location details view.
 */

public final class CAGMainLocationDetailsView
  implements CAGViewType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMainLocationDetailsView.class);

  private final CAGControllerType controller;
  private final CAGStringsType strings;
  private final CAGLocationAttachmentAddDialogs attachmentAddDialogs;
  private CAGViewAndStage<CAGLocationAttachmentAddView> attachmentAddDialog;

  @FXML private TabPane mainItemDetails;
  @FXML private TextField idField;
  @FXML private TextField nameField;
  @FXML private ImageView thumbnail;
  @FXML private ProgressBar thumbnailLoading;
  @FXML private TableView<CAMetadataType> meta;
  @FXML private ListView<CAAttachment> attachments;
  @FXML private Button metaAdd;
  @FXML private Button metaRemove;
  @FXML private Button attachmentAdd;
  @FXML private Button attachmentRemove;

  /**
   * The main location details view.
   *
   * @param services The service directory
   */

  public CAGMainLocationDetailsView(
    final RPServiceDirectoryType services)
  {
    this.controller =
      services.requireService(CAGControllerType.class);
    this.strings =
      services.requireService(CAGStringsType.class);
    this.attachmentAddDialogs =
      services.requireService(CAGLocationAttachmentAddDialogs.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.mainItemDetails.setDisable(true);

    this.thumbnail.setVisible(false);
    this.thumbnailLoading.setVisible(false);

    final var tableColumns =
      this.meta.getColumns();

    final var tablePkgColumn =
      (TableColumn<CAMetadataType, RDottedName>) tableColumns.get(0);
    final var tableTypeColumn =
      (TableColumn<CAMetadataType, RDottedName>) tableColumns.get(1);
    final var tableFieldColumn =
      (TableColumn<CAMetadataType, RDottedName>) tableColumns.get(2);
    final var tableValueColumn =
      (TableColumn<CAMetadataType, String>) tableColumns.get(3);

    tablePkgColumn.setSortable(true);
    tablePkgColumn.setReorderable(false);
    tablePkgColumn.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().typeName().packageName()
        );
      });

    tableTypeColumn.setSortable(true);
    tableTypeColumn.setReorderable(false);
    tableTypeColumn.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().typeName().typeName()
        );
      });

    tableFieldColumn.setSortable(true);
    tableFieldColumn.setReorderable(false);
    tableFieldColumn.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().fieldName()
        );
      });

    tableValueColumn.setSortable(true);
    tableValueColumn.setReorderable(false);
    tableValueColumn.setCellValueFactory(
      param -> {
        return new SimpleStringProperty(
          param.getValue().valueString()
        );
      });

    this.controller.locationSelectedMetadata()
      .comparatorProperty()
      .bind(this.meta.comparatorProperty());

    this.meta.setItems(this.controller.locationSelectedMetadata());

    this.attachments.setCellFactory(
      new CAGItemAttachmentCellFactory(this.strings)
    );
    this.attachments.setItems(
      this.controller.locationSelectedAttachments()
    );

    this.controller.locationSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.locationSelectionChanged(newValue);
      });

    this.controller.locationSelectedAttachments()
      .addListener((ListChangeListener<? super CAAttachment>) c -> {
        this.loadThumbnail();
      });
  }

  private void locationSelectionChanged(
    final CALocationSummary newValue)
  {
    if (newValue == null) {
      this.mainItemDetails.setDisable(true);
      this.idField.setText("");
      this.nameField.setText("");
      this.clearThumbnail();
      return;
    }

    this.mainItemDetails.setDisable(false);
    this.idField.setText(newValue.id().toString());
    this.nameField.setText(newValue.name());
    this.clearThumbnail();
  }

  private void clearThumbnail()
  {
    this.thumbnailLoading.setVisible(false);
    this.thumbnail.setVisible(false);
    this.thumbnail.setImage(null);
  }

  private void loadThumbnail()
  {
    this.attachments.getItems()
      .stream()
      .filter(a -> Objects.equals(a.relation(), CAAttachmentRelations.image()))
      .findFirst()
      .ifPresent(this::loadThumbnailFromAttachment);
  }

  private void loadThumbnailFromAttachment(
    final CAAttachment attachment)
  {
    LOG.debug("Loading thumbnail: {}", attachment.file().id());

    Platform.runLater(() -> {
      this.thumbnailLoading.setVisible(true);
      this.thumbnail.setVisible(false);
    });

    try {
      final var attachmentFile =
        attachment.file();
      final var fileID =
        attachmentFile.id();
      final var file =
        Files.createTempFile("cardant-gui", ".jpg");
      final var fileTmp =
        Files.createTempFile("cardant-gui", ".jpg");
      final var size =
        attachmentFile.size();
      final var hashAlgo =
        attachmentFile.hashAlgorithm();
      final var hashValue =
        attachmentFile.hashValue();

      this.controller.imageGet(
          fileID,
          file,
          fileTmp,
          size,
          hashAlgo,
          hashValue,
          (int) this.thumbnail.getFitWidth(),
          (int) this.thumbnail.getFitHeight())
        .thenAccept(image -> {
          Platform.runLater(() -> {
            this.thumbnailLoading.setVisible(false);
            this.thumbnail.setImage(image);
            this.thumbnail.setVisible(true);
          });
        });
    } catch (final IOException e) {
      LOG.debug("Loading thumbnail: ", e);
      Platform.runLater(this::clearThumbnail);
    }
  }

  @FXML
  private void onMetaAddSelected()
  {

  }

  @FXML
  private void onMetaRemoveSelected()
  {

  }

  @FXML
  private void onAttachmentAddSelected()
    throws IOException
  {
    if (this.attachmentAddDialog == null) {
      this.attachmentAddDialog =
        this.attachmentAddDialogs.createDialog(null);
    }

    this.attachmentAddDialog.stage()
      .show();
  }

  @FXML
  private void onAttachmentRemoveSelected()
  {

  }

  @FXML
  private void onItemNameSetSelected()
  {

  }
}
