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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.stream.Stream;

/**
 * Functions over tree items.
 */

public final class CAGTreeItems
{
  private CAGTreeItems()
  {

  }

  /**
   * @param tree The tree view
   * @param <T>  The type of item values
   *
   * @return The tree nodes
   */

  public static <T> Stream<TreeItem<T>> treeViewNodes(
    final TreeView<T> tree)
  {
    return treeNodes(tree.getRoot());
  }

  /**
   * @param root The root item
   * @param <T>  The type of item values
   *
   * @return The tree nodes
   */

  public static <T> Stream<TreeItem<T>> treeNodes(
    final TreeItem<T> root)
  {
    if (root == null) {
      return Stream.of();
    }

    return Stream.concat(
      Stream.of(root),
      root.getChildren()
        .stream()
        .flatMap(CAGTreeItems::treeNodes)
    );
  }
}
