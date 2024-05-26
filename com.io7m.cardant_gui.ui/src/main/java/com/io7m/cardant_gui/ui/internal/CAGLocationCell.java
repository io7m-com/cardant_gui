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

import com.io7m.cardant.model.CALocationSummary;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeCell;

import java.util.Objects;

/**
 * A tree cell.
 */

public final class CAGLocationCell
  extends TreeCell<CALocationSummary>
{
  private final CAGStringsType strings;
  private final Parent root;
  private final CAGLocationCellController controller;

  /**
   * Construct a cell.
   *
   * @param inStrings      The string resources
   * @throws Exception On errors
   */

  public CAGLocationCell(
    final CAGStringsType inStrings)
    throws Exception
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");

    final FXMLLoader loader =
      new FXMLLoader(
        CAGLocationCell.class.getResource(
          "/com/io7m/cardant_gui/ui/internal/locationCell.fxml")
      );

    loader.setResources(this.strings.resources());
    loader.setControllerFactory(param -> {
      return new CAGLocationCellController();
    });
    this.root = loader.load();
    this.controller = loader.getController();
  }

  @Override
  protected void updateItem(
    final CALocationSummary item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    if (empty || item == null) {
      this.setGraphic(null);
      this.setText(null);
      this.controller.unsetItem();
      return;
    }

    this.controller.setItem(item);
    this.setGraphic(this.root);
    this.setText(null);
  }
}
