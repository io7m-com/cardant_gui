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
import com.io7m.repetoir.core.RPServiceType;
import javafx.beans.value.ObservableValue;

import java.nio.file.Path;

/**
 * The file transfer controller.
 */

public interface CAGFileTransferControllerType
  extends RPServiceType
{
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
    String hashValue
  );

  /**
   * @return The current transfer status
   */

  ObservableValue<CAGTransferStatusType> transferStatus();
}
