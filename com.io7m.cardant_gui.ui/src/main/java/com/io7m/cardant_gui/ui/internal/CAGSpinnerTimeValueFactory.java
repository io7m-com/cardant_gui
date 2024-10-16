/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import javafx.scene.control.SpinnerValueFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * A spinner factory for dates.
 */

public final class CAGSpinnerTimeValueFactory
  extends SpinnerValueFactory<OffsetDateTime>
{
  private static final OffsetDateTime MIDNIGHT =
    OffsetDateTime.of(
      2000,
      1,
      1,
      0,
      0,
      0,
      0,
      ZoneOffset.UTC
    );

  /**
   * A spinner factory for dates.
   */

  public CAGSpinnerTimeValueFactory()
  {
    this.setValue(MIDNIGHT);
  }

  /**
   * Set the starting time.
   *
   * @param newTime The time
   */

  public void setTime(
    final OffsetDateTime newTime)
  {
    this.setValue(newTime);
  }

  @Override
  public void decrement(
    final int steps)
  {
    this.setValue(this.getValue().minusSeconds(steps));
  }

  @Override
  public void increment(
    final int steps)
  {
    this.setValue(this.getValue().plusSeconds(steps));
  }
}
