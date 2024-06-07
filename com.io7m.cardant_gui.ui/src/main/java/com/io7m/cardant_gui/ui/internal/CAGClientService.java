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

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientConnectionParameters;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.idstore.model.IdName;
import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.jattribute.core.AttributeType;
import com.io7m.jattribute.core.Attributes;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import static com.io7m.cardant_gui.ui.internal.CAGClientStatus.CONNECTED;
import static com.io7m.cardant_gui.ui.internal.CAGClientStatus.CONNECTING;
import static com.io7m.cardant_gui.ui.internal.CAGClientStatus.NOT_CONNECTED;
import static com.io7m.cardant_gui.ui.internal.CAGStatusEvent.Kind.ERROR;
import static com.io7m.cardant_gui.ui.internal.CAGStatusEvent.Kind.IDLE;
import static com.io7m.cardant_gui.ui.internal.CAGStatusEvent.Kind.RUNNING;
import static com.io7m.cardant_gui.ui.internal.CAGStatusEvent.Kind.RUNNING_LONG;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOGIN_CONNECTED;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOGIN_CONNECTING;

/**
 * The cardant client service.
 */

public final class CAGClientService
  implements CAGClientServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGClientService.class);

  private final AttributeType<CAGClientStatus> status;
  private final ExecutorService executor;
  private final Semaphore commandSemaphore;
  private final CAClientType client;
  private final CAGStatusService statusService;
  private final CAGStringsType strings;
  private final Semaphore transferSemaphore;
  private final Semaphore imageSemaphore;

  /**
   * The cardant client service.
   *
   * @param inStatusService The status service
   * @param inStrings       The strings
   */

  public CAGClientService(
    final CAGStatusService inStatusService,
    final CAGStringsType inStrings)
    throws CAClientException
  {
    this.statusService =
      Objects.requireNonNull(inStatusService, "statusService");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");

    this.status =
      Attributes.create(throwable -> LOG.error("Exception: ", throwable))
        .withValue(NOT_CONNECTED);

    this.executor =
      Executors.newThreadPerTaskExecutor(
        Thread.ofVirtual()
          .name("com.io7m.cardant_gui.client-", 0L)
          .factory()
      );

    this.commandSemaphore =
      new Semaphore(1);
    this.transferSemaphore =
      new Semaphore(1);
    this.imageSemaphore =
      new Semaphore(1);

    this.client =
      new CAClients()
        .create(new CAClientConfiguration(
          Locale.getDefault(),
          Clock.systemUTC()
        ));
  }

  @Override
  public AttributeReadableType<CAGClientStatus> status()
  {
    return this.status;
  }

  @Override
  public String toString()
  {
    return "[CAGClientService 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }

  @Override
  public String description()
  {
    return "Cardant client service.";
  }

  @Override
  public void login(
    final String host,
    final int port,
    final boolean https,
    final String username,
    final String password)
  {
    LOG.debug("Login: {}", host);

    this.executor.execute(() -> {
      try {
        this.commandSemaphore.acquire();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }

      try {
        final var connectUsername =
          new IdName(username);

        this.status.set(CONNECTING);
        this.statusService.publish(
          RUNNING_LONG,
          this.strings.format(CARDANT_LOGIN_CONNECTING, host)
        );

        this.client.connectOrThrow(
          new CAClientConnectionParameters(
            host,
            port,
            https,
            connectUsername,
            password,
            Map.of(),
            Duration.ofSeconds(10L),
            Duration.ofSeconds(10L)
          )
        );

        this.status.set(CONNECTED);
        this.statusService.publish(
          IDLE,
          this.strings.format(CARDANT_LOGIN_CONNECTED, host)
        );

      } catch (final Exception e) {
        LOG.debug("Login: Exception: ", e);
        this.status.set(NOT_CONNECTED);
        this.statusService.publish(ERROR, e.getMessage());
      } finally {
        this.commandSemaphore.release();
      }
    });
  }

  @Override
  public <R extends CAIResponseType> CompletableFuture<R> execute(
    final CAICommandType<R> command)
  {
    Objects.requireNonNull(command, "command");

    final var future = new CompletableFuture<R>();
    LOG.debug("Execute: {}", command);

    this.executor.execute(() -> {
      try {
        this.commandSemaphore.acquire();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
        return;
      }

      try {
        this.statusService.publish(RUNNING, "Executing command…");
        future.complete(
          this.client.sendAndWaitOrThrow(command, Duration.ofSeconds(30L))
        );
        this.statusService.publish(IDLE, "Executed command.");
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
      } catch (final Exception e) {
        LOG.debug("Execute: Exception: ", e);
        this.statusService.publish(ERROR, e.getMessage());
        future.completeExceptionally(e);
      } finally {
        this.commandSemaphore.release();
      }
    });

    return future;
  }

  @Override
  public CompletableFuture<Void> fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
  {
    final var future = new CompletableFuture<Void>();

    this.executor.execute(() -> {
      try {
        this.transferSemaphore.acquire();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
        return;
      }

      try {
        this.client.fileUpload(
          fileID, file, contentType, description, statistics
        );
        future.complete(null);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
      } catch (final Exception e) {
        this.statusService.publish(ERROR, e.getMessage());
        future.completeExceptionally(e);
      } finally {
        this.transferSemaphore.release();
      }
    });

    return future;
  }

  @Override
  public CompletableFuture<Void> fileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
  {
    final var future = new CompletableFuture<Void>();

    this.executor.execute(() -> {
      try {
        this.transferSemaphore.acquire();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
        return;
      }

      try {
        this.client.fileDownload(
          fileID,
          file,
          fileTmp,
          size,
          hashAlgorithm,
          hashValue,
          statistics
        );
        future.complete(null);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
      } catch (final Exception e) {
        this.statusService.publish(ERROR, e.getMessage());
        future.completeExceptionally(e);
      } finally {
        this.transferSemaphore.release();
      }
    });

    return future;
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
    final var future = new CompletableFuture<Image>();

    this.executor.execute(() -> {
      try {
        this.imageSemaphore.acquire();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
        return;
      }

      try {
        this.client.fileDownload(
          fileID,
          file,
          fileTmp,
          size,
          hashAlgorithm,
          hashValue,
          statistics -> {
            // Ignored
          }
        );

        future.complete(
          new Image(
            file.toUri().toString(),
            (double) width,
            (double) height,
            false,
            true,
            true)
        );
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        future.cancel(true);
      } catch (final Exception e) {
        this.statusService.publish(ERROR, e.getMessage());
        future.completeExceptionally(e);
      } finally {
        this.imageSemaphore.release();
      }
    });

    return future;
  }
}
