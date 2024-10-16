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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Controller methods for the item details view.
 */

public interface CAGItemDetailsControllerType
  extends AutoCloseable
{
  /**
   * @return The items for the current search query
   */

  ObservableList<CAItemSummary> itemsView();

  /**
   * @return The items for the current search query
   */

  SortedList<CAItemSummary> itemsViewSorted();

  /**
   * Select and fetch an item.
   *
   * @param id The item ID
   */

  void itemSelect(CAItemID id);

  /**
   * Clear the current item selection.
   */

  void itemSelectNothing();

  /**
   * Add an attachment.
   *
   * @param command The command
   */

  void itemAttachmentAdd(
    CAICommandItemAttachmentAdd command);

  /**
   * @return The currently selected item
   */

  CAGItemModelReadableType itemSelected();

  /**
   * Create an item.
   *
   * @param id   The item ID
   * @param name The name
   */

  void itemCreate(
    CAItemID id,
    String name);

  /**
   * Delete an item.
   *
   * @param id The item ID
   */

  void itemDelete(
    CAItemID id);
}
