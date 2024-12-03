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
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ATTACHMENTREMOVE_CONFIRM;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMMETADATA_DELETE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_TYPEUNASSIGN_CONFIRM;

/**
 * The main item details view.
 */

public final class CAGItemDetailsView
  implements CAGViewType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGItemDetailsView.class);

  private final CAGStringsType strings;
  private final CAGItemAttachmentAddDialogs attachmentAddDialogs;
  private final CAGClientServiceType client;
  private final CAGMetadataAddDialogs metadataAddDialogs;
  private final CAGItemNameSetDialogs itemNameSetDialogs;
  private final CAGTypeAssignDialogs typeAssignDialogs;
  private CAGItemDetailsControllerType itemDetailsController;
  private CAGViewAndStage<CAGItemAttachmentAddView> attachmentAddDialog;

  @FXML private TabPane mainItemDetails;
  @FXML private TextField idField;
  @FXML private TextField nameField;
  @FXML private ImageView thumbnail;
  @FXML private ProgressBar thumbnailLoading;
  @FXML private ListView<CAAttachment> attachments;
  @FXML private Button metaAdd;
  @FXML private Button metaRemove;
  @FXML private Button attachmentAdd;
  @FXML private Button attachmentRemove;
  @FXML private Button typeAssign;
  @FXML private Button typeUnassign;
  @FXML private TableView<CATypeRecordIdentifier> types;
  @FXML private TableColumn<CATypeRecordIdentifier, String> typesColPkg;
  @FXML private TableColumn<CATypeRecordIdentifier, String> typesColType;
  @FXML private TableView<CAMetadataType> meta;
  @FXML private TableColumn<CAMetadataType, RDottedName> metaColPkg;
  @FXML private TableColumn<CAMetadataType, RDottedName> metaColType;
  @FXML private TableColumn<CAMetadataType, RDottedName> metaColField;
  @FXML private TableColumn<CAMetadataType, String> metaColValue;

  /**
   * The main item details view.
   *
   * @param services The service directory
   */

  public CAGItemDetailsView(
    final RPServiceDirectoryType services)
  {
    this.client =
      services.requireService(CAGClientServiceType.class);
    this.strings =
      services.requireService(CAGStringsType.class);
    this.attachmentAddDialogs =
      services.requireService(CAGItemAttachmentAddDialogs.class);
    this.metadataAddDialogs =
      services.requireService(CAGMetadataAddDialogs.class);
    this.itemNameSetDialogs =
      services.requireService(CAGItemNameSetDialogs.class);
    this.typeAssignDialogs =
      services.requireService(CAGTypeAssignDialogs.class);
  }

  /**
   * Set the controllers.
   *
   * @param newController The controller
   */

  public void setControllers(
    final CAGItemDetailsControllerType newController)
  {
    this.itemDetailsController =
      Objects.requireNonNull(newController, "newController");

    final var itemSelected =
      this.itemDetailsController.itemSelected();

    itemSelected.metadata()
      .comparatorProperty()
      .bind(this.meta.comparatorProperty());

    this.meta.setItems(itemSelected.metadata());
    this.types.setItems(itemSelected.types());

    this.attachments.setCellFactory(
      new CAGItemAttachmentCellFactory(this.strings)
    );
    this.attachments.setItems(itemSelected.attachments());

    itemSelected.summary()
      .addListener((_, _, newValue) -> {
        this.itemSelectionChanged(newValue);
      });

    itemSelected.attachments()
      .addListener((ListChangeListener<? super CAAttachment>) c -> {
        this.loadThumbnail();
      });

    this.meta.getSelectionModel()
      .selectedItemProperty()
      .addListener(_ -> {
        this.onMetaSelectionChanged();
      });
  }

  private void onMetaSelectionChanged()
  {
    final var selected =
      this.meta.getSelectionModel()
        .getSelectedItem();

    if (selected == null) {
      this.metaRemove.setDisable(true);
      return;
    }

    this.metaRemove.setDisable(false);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.mainItemDetails.setDisable(true);

    this.thumbnail.setVisible(false);
    this.thumbnailLoading.setVisible(false);

    this.typesColPkg.setSortable(true);
    this.typesColPkg.setReorderable(false);
    this.typesColPkg.setCellValueFactory(
      param -> {
        return new SimpleStringProperty(param.getValue().packageName().value());
      });

    this.typesColType.setSortable(true);
    this.typesColType.setReorderable(false);
    this.typesColType.setCellValueFactory(
      param -> {
        return new SimpleStringProperty(param.getValue().typeName().value());
      });

    this.metaColPkg.setSortable(true);
    this.metaColPkg.setReorderable(false);
    this.metaColPkg.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().typeName().packageName()
        );
      });

    this.metaColType.setSortable(true);
    this.metaColType.setReorderable(false);
    this.metaColType.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().typeName().typeName()
        );
      });

    this.metaColField.setSortable(true);
    this.metaColField.setReorderable(false);
    this.metaColField.setCellValueFactory(
      param -> {
        return new SimpleObjectProperty<>(
          param.getValue().name().fieldName()
        );
      });

    this.metaColValue.setSortable(true);
    this.metaColValue.setReorderable(false);
    this.metaColValue.setCellValueFactory(
      param -> {
        return new SimpleStringProperty(
          param.getValue().valueString()
        );
      });

    this.types.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CATypeRecordIdentifier>)
                     this::itemTypeSelectionChanged);
  }

  private void itemTypeSelectionChanged(
    final ListChangeListener.Change<? extends CATypeRecordIdentifier> c)
  {
    this.typeUnassign.setDisable(true);

    final var selected = c.getList();
    if (selected.isEmpty()) {
      return;
    }

    this.typeUnassign.setDisable(false);
  }

  private void itemSelectionChanged(
    final Optional<CAItemSummary> newOpt)
  {
    if (newOpt.isEmpty()) {
      this.metaAdd.setDisable(true);
      this.metaRemove.setDisable(true);
      this.mainItemDetails.setDisable(true);
      this.idField.setText("");
      this.nameField.setText("");
      this.clearThumbnail();
      return;
    }

    final var newValue = newOpt.orElseThrow();
    this.mainItemDetails.setDisable(false);
    this.metaAdd.setDisable(false);
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

      this.client.imageGet(
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
    throws IOException
  {
    this.metadataAddDialogs.openDialogAndWait(
      new CAGMetadataAddDialogArguments(
        this.itemDetailsController.itemSelected()
          .summary()
          .getValue()
          .get()
          .id(),
        this.itemDetailsController
      )
    );
  }

  @FXML
  private void onMetaRemoveSelected()
  {
    final var alert =
      new Alert(
        Alert.AlertType.CONFIRMATION,
        this.strings.format(CARDANT_ITEMMETADATA_DELETE)
      );

    CAGCSS.setCSS(alert.getDialogPane());
    final var r = alert.showAndWait();
    if (r.isPresent()) {
      if (r.get().equals(ButtonType.OK)) {
        final var selected =
          this.meta.getSelectionModel()
            .getSelectedItem();

        this.itemDetailsController.itemMetadataRemove(
          this.itemDetailsController.itemSelected()
            .summary()
            .getValue()
            .get()
            .id(),
          selected.name()
        ).whenComplete((_, exception) -> {
          if (exception != null) {
            Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
          }
        });
      }
    }
  }

  @FXML
  private void onAttachmentAddSelected()
    throws IOException
  {
    if (this.attachmentAddDialog == null) {
      this.attachmentAddDialog =
        this.attachmentAddDialogs.createDialog(
          new CAGItemAttachmentAddDialogArguments(
            this.itemDetailsController,
            this.itemDetailsController.itemSelected()
              .summary()
              .getValue()
              .orElseThrow()
              .id()
          )
        );
    }

    this.attachmentAddDialog.stage()
      .show();
  }

  @FXML
  private void onAttachmentRemoveSelected()
  {
    final var alert =
      new Alert(
        Alert.AlertType.CONFIRMATION,
        this.strings.format(CARDANT_ATTACHMENTREMOVE_CONFIRM)
      );

    CAGCSS.setCSS(alert.getDialogPane());
    final var r = alert.showAndWait();
    if (r.isPresent()) {
      if (r.get().equals(ButtonType.OK)) {
        final var attachment =
          this.attachments.getSelectionModel()
            .getSelectedItem();

        final var item =
          this.itemDetailsController.itemSelected()
            .summary()
            .getValue()
            .get()
            .id();

        this.itemDetailsController.itemAttachmentRemove(item, attachment)
          .whenComplete((_, exception) -> {
            if (exception != null) {
              Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
            }
          });
      }
    }
  }

  @FXML
  private void onItemNameSetSelected()
    throws IOException
  {
    final var item =
      this.itemDetailsController.itemSelected()
        .summary()
        .getValue()
        .get();

    this.itemNameSetDialogs.openDialogAndWait(
      new CAGItemNameSetDialogArguments(
        this.itemDetailsController,
        item.name(),
        item.id()
      )
    );
  }

  @FXML
  private void onTypeAssignSelected()
    throws IOException
  {
    final var item =
      this.itemDetailsController.itemSelected()
        .summary()
        .getValue()
        .get()
        .id();

    this.typeAssignDialogs.openDialogAndWait(
      new CAGTypeAssignDialogArguments(
        item,
        this.itemDetailsController
      )
    );
  }

  @FXML
  private void onTypeUnassignSelected()
  {
    final var alert =
      new Alert(
        Alert.AlertType.CONFIRMATION,
        this.strings.format(CARDANT_TYPEUNASSIGN_CONFIRM)
      );

    CAGCSS.setCSS(alert.getDialogPane());
    final var r = alert.showAndWait();
    if (r.isPresent()) {
      if (r.get().equals(ButtonType.OK)) {
        final var type =
          this.types.getSelectionModel()
            .getSelectedItem();

        final var item =
          this.itemDetailsController.itemSelected()
            .summary()
            .getValue()
            .get()
            .id();

        this.itemDetailsController.itemTypeUnassign(item, type)
          .whenComplete((_, exception) -> {
            if (exception != null) {
              Platform.runLater(() -> CAGErrors.showThrowableAndWait(exception));
            }
          });
      }
    }
  }
}
