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

import java.util.Objects;

/**
 * A status event.
 *
 * @param kind    The status kind
 * @param message The message
 */

public record CAGStatusEvent(
  Kind kind,
  String message)
{
  /**
   * The status kind.
   */

  enum Kind
  {
    /**
     * The status is an error.
     */

    ERROR,

    /**
     * The status is idle.
     */

    IDLE,

    /**
     * The status is running.
     */

    RUNNING,

    /**
     * The status is long-running.
     */

    RUNNING_LONG
  }

  /**
   * A status event.
   *
   * @param kind    The status kind
   * @param message The message
   */

  public CAGStatusEvent
  {
    Objects.requireNonNull(message, "message");
    Objects.requireNonNull(kind, "kind");
  }
}
