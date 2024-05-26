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
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Idle.IDLE;

/**
 * The main controller.
 */

public final class CAGController implements CAGControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGController.class);

  private final ObservableList<CAItemSummary> items;
  private final SortedList<CAItemSummary> itemsSorted;
  private final ObservableList<CAItemSummary> itemsRead;
  private final SimpleObjectProperty<CAGPageRange> itemPages;
  private final CAGClientServiceType clientService;
  private final SimpleObjectProperty<CAItemSummary> itemSelected;
  private final ObservableList<CAMetadataType> itemSelectedMeta;
  private final SortedList<CAMetadataType> itemSelectedMetaSorted;
  private final ObservableList<CAAttachment> itemSelectedAttachments;
  private final ObservableList<CAFileType.CAFileWithoutData> files;
  private final SimpleObjectProperty<CAGPageRange> filePages;
  private final SimpleObjectProperty<CAGTransferStatusType> transferStatus;
  private final SimpleObjectProperty<CAGPageRange> auditEventPages;
  private final ObservableList<CAAuditEvent> auditEvents;
  private final SortedList<CAAuditEvent> auditEventsSorted;

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
          data.items()
            .stream()
            .collect(Collectors.toList());

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
  public String toString()
  {
    return String.format(
      "[CAGController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
