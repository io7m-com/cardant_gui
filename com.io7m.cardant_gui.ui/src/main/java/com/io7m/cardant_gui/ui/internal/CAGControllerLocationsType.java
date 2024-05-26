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
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.repetoir.core.RPServiceType;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TreeItem;

/**
 * Location methods for the controller.
 */

public interface CAGControllerLocationsType
  extends RPServiceType
{
  /**
   * @return The location tree
   */

  ObservableValue<TreeItem<CALocationSummary>> locationTree();

  /**
   * Start searching for locations.
   */

  void locationSearchBegin();

  /**
   * Fetch an location.
   *
   * @param id The location ID
   */

  void locationGet(CALocationID id);

  /**
   * @return The metadata for the selected location
   */

  SortedList<CAMetadataType> locationSelectedMetadata();

  /**
   * Clear the current location selection.
   */

  void locationSelectNothing();

  /**
   * @return The attachments for the selected location
   */

  ObservableList<CAAttachment> locationSelectedAttachments();

  /**
   * Add an attachment.
   *
   * @param command The command
   */

  void locationAttachmentAdd(
    CAICommandLocationAttachmentAdd command);

  /**
   * @return The page range for the current location search query
   */

  ObservableValue<CAGPageRange> locationPages();

  /**
   * @return The currently selected location
   */

  ObservableValue<CALocationSummary> locationSelected();
  
  /**
   * Remove the selected location.
   */

  void locationRemove();
}
