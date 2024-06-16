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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

/**
 * A location tree controller.
 */

public interface CAGLocationTreeControllerType
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
   * Remove the selected location.
   *
   * @param location The location
   */

  void locationRemove(
    CALocationID location);

  /**
   * Create a location with a name.
   *
   * @param name The name
   */

  void locationCreate(String name);

  /**
   * Set the parent of {@code location} to {@code newParent}.
   *
   * @param location  The location
   * @param newParent The new parent
   */

  void locationReparent(
    CALocationID location,
    CALocationID newParent);

  /**
   * Select and fetch an location.
   *
   * @param id The location ID
   */

  void locationSelect(CALocationID id);

  /**
   * Clear the current location selection.
   */

  void locationSelectNothing();

  /**
   * Add an attachment.
   *
   * @param command The command
   */

  void locationAttachmentAdd(
    CAICommandLocationAttachmentAdd command);

  /**
   * @return The currently selected location
   */

  CAGLocationModelReadableType locationSelected();
}
