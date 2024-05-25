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

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A cell factory.
 */

public final class CAGMetaMatchCellFactory
  implements Callback<TreeView<CAGMetaMatchNodeType>, TreeCell<CAGMetaMatchNodeType>>
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMetaMatchCellFactory.class);

  private final CAGStringsType strings;
  private final CAGMetaMatchTreeSequenceType compilations;

  /**
   * A cell factory.
   *
   * @param inStrings      The string resources
   * @param inCompilations The tree sequence
   */

  public CAGMetaMatchCellFactory(
    final CAGStringsType inStrings,
    final CAGMetaMatchTreeSequenceType inCompilations)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.compilations =
      Objects.requireNonNull(inCompilations, "inCompilations");
  }

  @Override
  public TreeCell<CAGMetaMatchNodeType> call(
    final TreeView<CAGMetaMatchNodeType> param)
  {
    try {
      final var cell =
        new CAGMetaMatchCell(this.strings, this.compilations);

      LOG.trace(
        "[{}] create",
        Integer.toUnsignedString(cell.hashCode(), 16)
      );

      return cell;
    } catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
