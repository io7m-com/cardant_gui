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

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.repetoir.core.RPServiceType;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Item methods for the controller.
 */

public interface CAGControllerItemsType
  extends RPServiceType
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
   * Start searching for items.
   *
   * @param searchParameters The search parameters
   */

  void itemSearchBegin(
    CAItemSearchParameters searchParameters);

  /**
   * Fetch an item.
   *
   * @param id The item ID
   */

  void itemGet(CAItemID id);

  /**
   * @return The metadata for the selected item
   */

  SortedList<CAMetadataType> itemSelectedMetadata();

  /**
   * Clear the current item selection.
   */

  void itemSelectNothing();

  /**
   * @return The attachments for the selected item
   */

  ObservableList<CAAttachment> itemSelectedAttachments();

  /**
   * Add an attachment.
   *
   * @param command The command
   */

  void itemAttachmentAdd(
    CAICommandItemAttachmentAdd command);

  /**
   * @return The page range for the current item search query
   */

  ObservableValue<CAGPageRange> itemPages();

  /**
   * @return The currently selected item
   */

  ObservableValue<CAItemSummary> itemSelected();

  /**
   * @return The currently assigned types
   */

  SortedList<CATypeRecordIdentifier> itemSelectedTypes();
}
