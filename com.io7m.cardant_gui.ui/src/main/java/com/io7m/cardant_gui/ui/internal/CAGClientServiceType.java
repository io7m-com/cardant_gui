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
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.repetoir.core.RPServiceType;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The client service.
 */

public interface CAGClientServiceType extends RPServiceType
{
  /**
   * @return The current client status
   */

  AttributeReadableType<CAGClientStatus> status();

  /**
   * Asynchronously log in.
   *
   * @param host     The host
   * @param port     The port
   * @param https    {@code true} if HTTPS is enabled
   * @param username The username
   * @param password The password
   */

  void login(
    String host,
    int port,
    boolean https,
    String username,
    String password);

  /**
   * Execute the given command.
   *
   * @param command The command
   * @param <R>     The type of responses
   *
   * @return A future representing the operation in progress
   */

  <R extends CAIResponseType> CompletableFuture<R> execute(
    CAICommandType<R> command);

  /**
   * Execute a file upload.
   *
   * @param fileID      The file ID
   * @param file        The file
   * @param contentType The content type
   * @param description The description
   * @param statistics  A consumer of transfer statistics
   *
   * @return A future representing the operation in progress
   */

  CompletableFuture<Void> fileUpload(
    CAFileID fileID,
    Path file,
    String contentType,
    String description,
    Consumer<CAClientTransferStatistics> statistics
  );

  /**
   * Execute a file download.
   *
   * @param fileID        The file ID
   * @param file          The file
   * @param fileTmp       The temporary file
   * @param size          The expected size
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The hash value
   * @param statistics    A consumer of transfer statistics
   *
   * @return A future representing the operation in progress
   */

  CompletableFuture<Void> fileDownload(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    Consumer<CAClientTransferStatistics> statistics
  );

  /**
   * Fetch a file as an image.
   *
   * @param fileID        The file ID
   * @param file          The file
   * @param fileTmp       The temporary file
   * @param size          The expected size
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The hash value
   * @param width         The image width
   * @param height        The image height
   *
   * @return A future representing the operation in progress
   */

  CompletableFuture<Image> imageGet(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    int width,
    int height
  );
}
