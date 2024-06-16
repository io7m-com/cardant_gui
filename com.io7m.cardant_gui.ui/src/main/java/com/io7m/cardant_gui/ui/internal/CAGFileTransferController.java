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
import com.io7m.cardant.model.CAFileID;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.nio.file.Path;
import java.util.Objects;

import static com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Idle.IDLE;

/**
 * A file transfer controller.
 */

public final class CAGFileTransferController
  implements CAGFileTransferControllerType
{
  private final CAGClientServiceType client;
  private final SimpleObjectProperty<CAGTransferStatusType> transferStatus;

  /**
   * A file transfer controller.
   *
   * @param inClient The client
   *
   * @return The controller
   */

  public static CAGFileTransferControllerType create(
    final CAGClientServiceType inClient)
  {
    return new CAGFileTransferController(inClient);
  }

  private CAGFileTransferController(
    final CAGClientServiceType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.transferStatus =
      new SimpleObjectProperty<>(IDLE);
  }

  @Override
  public void fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description)
  {
    final var future =
      this.client.fileUpload(
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
      this.client.fileDownload(
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
  public ObservableValue<CAGTransferStatusType> transferStatus()
  {
    return this.transferStatus;
  }

  @Override
  public String description()
  {
    return "File transfer service.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGFileTransferController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
