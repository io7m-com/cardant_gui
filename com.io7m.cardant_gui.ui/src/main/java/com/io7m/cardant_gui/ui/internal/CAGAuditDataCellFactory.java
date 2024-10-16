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

import com.io7m.cardant.model.CAAuditEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.Map;
import java.util.Objects;

/**
 * A cell factory.
 */

public final class CAGAuditDataCellFactory
  implements Callback<
  TableColumn<CAAuditEvent, Map<String, String>>,
  TableCell<CAAuditEvent, Map<String, String>>
  >
{
  private final CAGStringsType strings;

  /**
   * A cell factory.
   *
   * @param inStrings The string resources
   */

  public CAGAuditDataCellFactory(
    final CAGStringsType inStrings)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public TableCell<CAAuditEvent, Map<String, String>> call(
    final TableColumn<CAAuditEvent, Map<String, String>> param)
  {
    try {
      return new CAGAuditDataCell(this.strings);
    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
