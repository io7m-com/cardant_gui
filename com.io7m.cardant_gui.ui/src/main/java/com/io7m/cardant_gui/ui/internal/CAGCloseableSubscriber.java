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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

/**
 * A subscriber implementation that is closeable.
 *
 * @param <T> The type of received values
 */

public final class CAGCloseableSubscriber<T>
  implements Flow.Subscriber<T>, AutoCloseable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGCloseableSubscriber.class);

  private final Consumer<T> consumer;
  private Flow.Subscription subscription;

  private CAGCloseableSubscriber(
    final Consumer<T> inConsumer)
  {
    this.consumer =
      Objects.requireNonNull(inConsumer, "inConsumer");
  }

  /**
   * A subscriber implementation that is closeable.
   *
   * @param inConsumer The value consumer
   * @param <T>        The type of received values
   *
   * @return The subscriber
   */

  public static <T> CAGCloseableSubscriber<T> create(
    final Consumer<T> inConsumer)
  {
    return new CAGCloseableSubscriber<>(inConsumer);
  }

  @Override
  public void close()
  {
    this.subscription.cancel();
  }

  @Override
  public void onSubscribe(
    final Flow.Subscription inSubscription)
  {
    this.subscription =
      Objects.requireNonNull(inSubscription, "subscription");
  }

  @Override
  public void onNext(
    final T item)
  {
    this.consumer.accept(Objects.requireNonNull(item, "item"));
  }

  @Override
  public void onError(
    final Throwable throwable)
  {
    LOG.error("onError: ", throwable);
  }

  @Override
  public void onComplete()
  {

  }
}
