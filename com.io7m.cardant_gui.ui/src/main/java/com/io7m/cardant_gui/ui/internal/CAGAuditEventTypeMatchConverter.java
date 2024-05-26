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

import javafx.util.StringConverter;

import java.util.Objects;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_EQUALTO;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_NOTEQUALTO;

/**
 * A string converter.
 */

public final class CAGAuditEventTypeMatchConverter
  extends StringConverter<CAGAuditEventTypeMatchKind>
{
  private final CAGStringsType strings;

  /**
   * A string converter.
   *
   * @param inStrings The string resources
   */

  public CAGAuditEventTypeMatchConverter(
    final CAGStringsType inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public String toString(
    final CAGAuditEventTypeMatchKind k)
  {
    if (k == null) {
      return "";
    }

    return switch (k) {
      case ANY -> {
        yield this.strings.format(CARDANT_EXACT_ANY);
      }
      case EQUAL_TO -> {
        yield this.strings.format(CARDANT_EXACT_EQUALTO);
      }
      case NOT_EQUAL_TO -> {
        yield this.strings.format(CARDANT_EXACT_NOTEQUALTO);
      }
    };
  }

  @Override
  public CAGAuditEventTypeMatchKind fromString(
    final String string)
  {
    return null;
  }
}
