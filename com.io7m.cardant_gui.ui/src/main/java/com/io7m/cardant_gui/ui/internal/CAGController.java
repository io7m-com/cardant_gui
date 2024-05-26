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

import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Idle.IDLE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The main controller.
 */

public final class CAGController implements CAGControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGController.class);

  private static final CALocationID ROOT_LOCATION =
    CALocationID.of("00000000-0000-0000-0000-000000000000");

  private static final CALocationSummary ROOT_LOCATION_SUMMARY =
    new CALocationSummary(ROOT_LOCATION, Optional.empty(), "Everywhere");

  private final CAGClientServiceType clientService;
  private final ObservableList<CAAttachment> itemSelectedAttachments;
  private final ObservableList<CAAuditEvent> auditEvents;
  private final ObservableList<CAFileType.CAFileWithoutData> files;
  private final ObservableList<CAItemSummary> items;
  private final ObservableList<CAItemSummary> itemsRead;
  private final ObservableList<CAMetadataType> itemSelectedMeta;
  private final ObservableList<CATypePackageSummary> typePackages;
  private final SimpleObjectProperty<CAGPageRange> auditEventPages;
  private final SimpleObjectProperty<CAGPageRange> filePages;
  private final SimpleObjectProperty<CAGPageRange> itemPages;
  private final SimpleObjectProperty<CAGPageRange> typePackagePages;
  private final SimpleObjectProperty<CAGTransferStatusType> transferStatus;
  private final SimpleObjectProperty<CAItemSummary> itemSelected;
  private final SimpleStringProperty typePackageTextSelected;
  private final SortedList<CAAuditEvent> auditEventsSorted;
  private final SortedList<CAItemSummary> itemsSorted;
  private final SortedList<CAMetadataType> itemSelectedMetaSorted;
  private final SortedList<CATypePackageSummary> typePackagesSorted;
  private final ObservableList<CAMetadataType> locationSelectedMeta;
  private final SortedList<CAMetadataType> locationSelectedMetaSorted;
  private final ObservableList<CAAttachment> locationSelectedAttachments;
  private final SimpleObjectProperty<CAGPageRange> locationPages;
  private final SimpleObjectProperty<CALocationSummary> locationSelected;
  private final SimpleObjectProperty<TreeItem<CALocationSummary>> locationTree;

  private CAGController(
    final CAGClientServiceType inClientService)
  {
    this.clientService =
      Objects.requireNonNull(inClientService, "clientService");

    this.items =
      FXCollections.observableArrayList();
    this.itemsSorted =
      new SortedList<>(this.items);
    this.itemsRead =
      FXCollections.unmodifiableObservableList(this.items);

    this.itemPages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.itemSelected =
      new SimpleObjectProperty<>();

    this.itemSelectedMeta =
      FXCollections.observableArrayList();
    this.itemSelectedMetaSorted =
      new SortedList<>(this.itemSelectedMeta);
    this.itemSelectedAttachments =
      FXCollections.observableArrayList();

    this.filePages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.files =
      FXCollections.observableArrayList();

    this.transferStatus =
      new SimpleObjectProperty<>(IDLE);

    this.auditEventPages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.auditEvents =
      FXCollections.observableArrayList();
    this.auditEventsSorted =
      new SortedList<>(this.auditEvents);

    this.typePackagePages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.typePackages =
      FXCollections.observableArrayList();
    this.typePackagesSorted =
      new SortedList<>(this.typePackages);

    this.typePackageTextSelected =
      new SimpleStringProperty();

    this.locationSelectedMeta =
      FXCollections.observableArrayList();
    this.locationSelectedMetaSorted =
      new SortedList<>(this.locationSelectedMeta);
    this.locationSelectedAttachments =
      FXCollections.observableArrayList();

    this.locationPages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.locationSelected =
      new SimpleObjectProperty<>();

    this.locationTree =
      new SimpleObjectProperty<>();
  }

  /**
   * Create a controller.
   *
   * @param clients The client service
   *
   * @return A controller
   */

  public static CAGControllerType create(
    final CAGClientServiceType clients)
  {
    final var controller = new CAGController(clients);
    clients.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.items.clear();
    this.auditEvents.clear();
    this.files.clear();
  }

  @Override
  public String description()
  {
    return "Main controller";
  }

  @Override
  public ObservableList<CAItemSummary> itemsView()
  {
    return this.itemsRead;
  }

  @Override
  public SortedList<CAItemSummary> itemsViewSorted()
  {
    return this.itemsSorted;
  }

  @Override
  public void itemSearchBegin(
    final CAItemSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandItemSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data =
          response.data();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} items", newItemPage.size());
        this.itemPages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.items.setAll(newItemPage);
      });
    });
  }

  @Override
  public void itemGet(
    final CAItemID id)
  {
    final var future =
      this.clientService.execute(new CAICommandItemGet(id));

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var item = response.data();

        this.itemSelected.set(item.summary());

        this.itemSelectedMeta.setAll(
          item.metadata()
            .values()
            .stream()
            .toList()
        );

        this.itemSelectedAttachments.setAll(
          item.attachments()
            .values()
            .stream()
            .sorted(Comparator.comparing(o -> o.key().fileID()))
            .collect(Collectors.toList())
        );
      });
    });
  }

  @Override
  public ObservableValue<CAGPageRange> itemPages()
  {
    return this.itemPages;
  }

  @Override
  public ObservableValue<CAItemSummary> itemSelected()
  {
    return this.itemSelected;
  }

  @Override
  public void typePackageGet(
    final CATypePackageIdentifier id)
  {
    final var future =
      this.clientService.execute(new CAICommandTypePackageGetText(id));

    future.thenAccept(response -> {
      final String formatted = formatXML(response.data());

      Platform.runLater(() -> {
        this.typePackageTextSelected.set(formatted);
      });
    });
  }

  private static String formatXML(
    final String data)
  {
    try {
      final Transformer transformer =
        TransformerFactory.newInstance()
          .newTransformer();

      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      final StreamResult result =
        new StreamResult(new StringWriter());
      final StreamSource source =
        new StreamSource(new ByteArrayInputStream(data.getBytes(UTF_8)));

      transformer.transform(source, result);
      return result.getWriter().toString();
    } catch (final TransformerException e) {
      final var sw = new StringWriter();
      final var pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      return sw.toString();
    }
  }

  @Override
  public ObservableValue<String> typePackageTextSelected()
  {
    return this.typePackageTextSelected;
  }

  @Override
  public void typePackageSelectNothing()
  {
    this.typePackageTextSelected.set(null);
  }

  @Override
  public SortedList<CAMetadataType> itemSelectedMetadata()
  {
    return this.itemSelectedMetaSorted;
  }

  @Override
  public void itemSelectNothing()
  {
    this.itemSelectedMeta.clear();
    this.itemSelected.set(null);
    this.itemSelectedAttachments.clear();
  }

  @Override
  public ObservableList<CAAttachment> itemSelectedAttachments()
  {
    return this.itemSelectedAttachments;
  }

  @Override
  public void fileSearchBegin(
    final CAFileSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandFileSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data =
          response.data();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} files", newItemPage.size());
        this.filePages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.files.setAll(newItemPage);
      });
    });
  }

  @Override
  public ObservableList<CAFileType.CAFileWithoutData> filesView()
  {
    return this.files;
  }

  @Override
  public ObservableValue<CAGTransferStatusType> transferStatus()
  {
    return this.transferStatus;
  }

  @Override
  public void fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description)
  {
    final var future =
      this.clientService.fileUpload(
        fileID,
        file,
        contentType,
        description,
        statistics -> {
          Platform.runLater(() -> this.onUploadStatisticsChanged(statistics));
        }
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> this.transferStatus.set(IDLE));
    });
  }

  @Override
  public void fileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue)
  {
    final var future =
      this.clientService.fileDownload(
        fileID,
        file,
        fileTmp,
        size,
        hashAlgorithm,
        hashValue,
        statistics -> {
          Platform.runLater(() -> this.onDownloadStatisticsChanged(statistics));
        }
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> this.transferStatus.set(IDLE));
    });
  }

  private void onDownloadStatisticsChanged(
    final CAClientTransferStatistics statistics)
  {
    this.transferStatus.set(new CAGTransferStatusType.Downloading(statistics));
  }

  private void onUploadStatisticsChanged(
    final CAClientTransferStatistics statistics)
  {
    this.transferStatus.set(new CAGTransferStatusType.Uploading(statistics));
  }

  @Override
  public ObservableValue<CAGPageRange> filePages()
  {
    return this.filePages;
  }

  @Override
  public CompletableFuture<Image> imageGet(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final int width,
    final int height)
  {
    return this.clientService.imageGet(
      fileID,
      file,
      fileTmp,
      size,
      hashAlgorithm,
      hashValue,
      width,
      height
    );
  }

  @Override
  public void itemAttachmentAdd(
    final CAICommandItemAttachmentAdd command)
  {
    this.clientService.execute(command);
    this.itemGet(command.item());
  }

  @Override
  public void auditSearchBegin(
    final CAAuditSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandAuditSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data = response.results();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} audit events", newItemPage.size());
        this.auditEventPages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.auditEvents.setAll(newItemPage);
      });
    });
  }

  @Override
  public ObservableList<CAAuditEvent> auditEventsView()
  {
    return this.auditEvents;
  }

  @Override
  public SortedList<CAAuditEvent> auditEventsViewSorted()
  {
    return this.auditEventsSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> auditEventsPages()
  {
    return this.auditEventPages;
  }

  @Override
  public void typePackageSearchBegin(
    final CATypePackageSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandTypePackageSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data = response.data();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} type packages", newItemPage.size());
        this.typePackagePages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.typePackages.setAll(newItemPage);
      });
    });
  }

  @Override
  public ObservableList<CATypePackageSummary> typePackagesView()
  {
    return this.typePackages;
  }

  @Override
  public SortedList<CATypePackageSummary> typePackagesViewSorted()
  {
    return this.typePackagesSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> typePackagesPages()
  {
    return this.typePackagePages;
  }

  @Override
  public void typePackageInstall(
    final Path file)
  {
    try {
      this.clientService.execute(
        new CAICommandTypePackageInstall(Files.readString(file, UTF_8))
      );
    } catch (final IOException e) {
      LOG.error("typePackageInstall: ", e);
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  @Override
  public ObservableValue<TreeItem<CALocationSummary>> locationTree()
  {
    return this.locationTree;
  }

  @Override
  public void locationSearchBegin()
  {
    final var future =
      this.clientService.execute(new CAICommandLocationList());

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data =
          response.data();
        final var summaries =
          data.locations();

        LOG.debug("Received {} locations", summaries.size());

        final var treeItems =
          new HashMap<CALocationID, TreeItem<CALocationSummary>>(summaries.size());
        final var newRoot =
          new TreeItem<>(ROOT_LOCATION_SUMMARY);

        for (final var location : summaries.values()) {
          final var item = new TreeItem<>(location);
          treeItems.put(location.id(), item);
        }

        for (final var location : summaries.values()) {
          final var locationItem =
            treeItems.get(location.id());
          final var parent =
            location.parent();

          if (parent.isEmpty()) {
            newRoot.getChildren().add(locationItem);
            continue;
          }

          final var parentId =
            parent.get();
          final var parentItem =
            treeItems.get(parentId);

          if (parentItem == null) {
            LOG.warn("Location {} provided a nonexistent parent {}", location.id(), parentId);
            continue;
          }

          parentItem.getChildren().add(locationItem);
        }

        this.locationTree.set(newRoot);
      });
    });
  }

  @Override
  public void locationGet(
    final CALocationID id)
  {
    if (Objects.equals(id, ROOT_LOCATION)) {
      this.locationSelectNothing();
      return;
    }

    final var future =
      this.clientService.execute(new CAICommandLocationGet(id));

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var location = response.data();

        this.locationSelected.set(location.summary());

        this.locationSelectedMeta.setAll(
          location.metadata()
            .values()
            .stream()
            .toList()
        );

        this.locationSelectedAttachments.setAll(
          location.attachments()
            .values()
            .stream()
            .sorted(Comparator.comparing(o -> o.key().fileID()))
            .collect(Collectors.toList())
        );
      });
    });
  }

  @Override
  public SortedList<CAMetadataType> locationSelectedMetadata()
  {
    return this.locationSelectedMetaSorted;
  }

  @Override
  public void locationSelectNothing()
  {
    this.locationSelected.set(null);
  }

  @Override
  public ObservableList<CAAttachment> locationSelectedAttachments()
  {
    return this.locationSelectedAttachments;
  }

  @Override
  public void locationAttachmentAdd(
    final CAICommandLocationAttachmentAdd command)
  {

  }

  @Override
  public ObservableValue<CAGPageRange> locationPages()
  {
    return this.locationPages;
  }

  @Override
  public ObservableValue<CALocationSummary> locationSelected()
  {
    return this.locationSelected;
  }

  @Override
  public void locationRemove()
  {
    final var location = this.locationSelected.get();
    if (location == null) {
      return;
    }

    throw new IllegalStateException("Unimplemented code!");
  }
}
