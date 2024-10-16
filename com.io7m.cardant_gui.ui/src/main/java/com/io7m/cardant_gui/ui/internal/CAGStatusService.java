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

import com.io7m.repetoir.core.RPServiceType;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

/**
 * The status service.
 */

public final class CAGStatusService implements RPServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGStatusService.class);

  private final SubmissionPublisher<CAGStatusEvent> events;

  /**
   * The status service.
   */

  public CAGStatusService()
  {
    this.events =
      new SubmissionPublisher<>();
  }

  /**
   * @return The status events.
   */

  public Flow.Publisher<CAGStatusEvent> events()
  {
    return this.events;
  }

  /**
   * Publish an event.
   *
   * @param event The event
   */

  public void publish(
    final CAGStatusEvent event)
  {
    this.events.submit(
      Objects.requireNonNull(event, "event")
    );
  }

  /**
   * Publish an event.
   *
   * @param kind The event kind
   * @param text The event text
   */

  public void publish(
    final CAGStatusEvent.Kind kind,
    final String text)
  {
    this.publish(new CAGStatusEvent(kind, text));
  }

  /**
   * Subscribe to status events.
   *
   * @param consumer The event consumer
   */

  public void subscribe(
    final Consumer<CAGStatusEvent> consumer)
  {
    Objects.requireNonNull(consumer, "consumer");
    this.events.subscribe(new CAGStatusEventSubscriber(consumer));
  }

  @Override
  public String toString()
  {
    return "[CAGStatusService 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }

  @Override
  public String description()
  {
    return "Status service.";
  }

  private static final class CAGStatusEventSubscriber
    implements Flow.Subscriber<CAGStatusEvent>
  {
    private final Consumer<CAGStatusEvent> consumer;

    CAGStatusEventSubscriber(
      final Consumer<CAGStatusEvent> inConsumer)
    {
      this.consumer =
        Objects.requireNonNull(inConsumer, "consumer");
    }

    @Override
    public void onSubscribe(
      final Flow.Subscription subscription)
    {
      subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(
      final CAGStatusEvent item)
    {
      Platform.runLater(() -> this.consumer.accept(item));
    }

    @Override
    public void onError(
      final Throwable throwable)
    {
      LOG.error("Exception: ", throwable);
    }

    @Override
    public void onComplete()
    {

    }
  }
}
