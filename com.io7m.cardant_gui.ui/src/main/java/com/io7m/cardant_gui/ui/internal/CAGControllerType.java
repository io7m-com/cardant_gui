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
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.repetoir.core.RPServiceType;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * The main controller interface.
 */

public interface CAGControllerType
  extends RPServiceType
{
  /**
   * @return The items for the current search query
   */

  ObservableList<CAItemSummary> itemsView();

  /**
   * @return The items for the current search query
   */

  SortedList<CAItemSummary> itemsViewSorted();

  /**
   * Start searching for items.
   *
   * @param searchParameters The search parameters
   */

  void itemSearchBegin(
    CAItemSearchParameters searchParameters);

  /**
   * Fetch an item.
   *
   * @param id The item ID
   */

  void itemGet(CAItemID id);

  /**
   * @return The metadata for the selected item
   */

  SortedList<CAMetadataType> itemSelectedMetadata();

  /**
   * Clear the current item selection.
   */

  void itemSelectNothing();

  /**
   * @return The attachments for the selected item
   */

  ObservableList<CAAttachment> itemSelectedAttachments();

  /**
   * Start searching for files.
   *
   * @param searchParameters The parameters
   */

  void fileSearchBegin(
    CAFileSearchParameters searchParameters);

  /**
   * @return The files for the current search query
   */

  ObservableList<CAFileType.CAFileWithoutData> filesView();

  /**
   * @return The current transfer status
   */

  ObservableValue<CAGTransferStatusType> transferStatus();

  /**
   * Upload a file.
   *
   * @param fileID      The file ID
   * @param file        The file
   * @param contentType The content type
   * @param description The description
   */

  void fileUpload(
    CAFileID fileID,
    Path file,
    String contentType,
    String description
  );

  /**
   * Download a file.
   *
   * @param fileID        The file ID
   * @param file          The output file
   * @param fileTmp       The output temporary file
   * @param size          The expected size
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The expected hash value
   */

  void fileDownload(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue);

  /**
   * @return The page range for the current file search query
   */

  ObservableValue<CAGPageRange> filePages();

  /**
   * Get an image.
   *
   * @param fileID        The file ID
   * @param file          The output file
   * @param fileTmp       The output temporary file
   * @param size          The expected size
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The expected hash value
   * @param width         The requested width
   * @param height        The requested height
   *
   * @return The operation in progress
   */

  CompletableFuture<Image> imageGet(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    int width,
    int height);

  /**
   * Add an attachment.
   *
   * @param command The command
   */

  void itemAttachmentAdd(
    CAICommandItemAttachmentAdd command);

  /**
   * Start searching for audit records.
   *
   * @param searchParameters The search parameters
   */

  void auditSearchBegin(
    CAAuditSearchParameters searchParameters);

  /**
   * @return The audit records for the current search query
   */

  ObservableList<CAAuditEvent> auditEventsView();

  /**
   * @return The audit records for the current search query
   */

  SortedList<CAAuditEvent> auditEventsViewSorted();

  /**
   * @return The page range for the current audit event search query
   */

  ObservableValue<CAGPageRange> auditEventsPages();

  /**
   * A page range.
   *
   * @param pageIndex The page index (indexed from 1)
   * @param pageCount The page count
   */

  record CAGPageRange(
    long pageIndex,
    long pageCount)
  {

  }

  /**
   * @return The page range for the current item search query
   */

  ObservableValue<CAGPageRange> itemPages();

  /**
   * @return The currently selected item
   */

  ObservableValue<CAItemSummary> itemSelected();
}
