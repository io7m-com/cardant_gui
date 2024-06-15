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
import com.io7m.cardant.model.CAStockOccurrenceType;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_COPY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SELECTALL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCK_OPENINLOCATION;

/**
 * A cell displaying a location.
 */

public final class CAGStockTableLocationCell
  extends TableCell<CAStockOccurrenceType, CALocationSummary>
{
  private final TextField textField = new TextField();
  private final Tooltip tooltip = new Tooltip();
  private final MenuItem menuItemCopy;
  private final ContextMenu customMenu;
  private final MenuItem menuItemLocOpen;
  private final MenuItem menuItemSelectAll;
  private CALocationSummary locationNow;

  /**
   * A cell displaying a location.
   *
   * @param controller The controller
   * @param strings    The string resources
   */

  public CAGStockTableLocationCell(
    final CAGControllerType controller,
    final CAGStringsType strings)
  {
    this.textField.setTooltip(this.tooltip);

    this.menuItemCopy =
      new MenuItem(strings.format(CARDANT_COPY));
    this.menuItemSelectAll =
      new MenuItem(strings.format(CARDANT_SELECTALL));
    this.menuItemLocOpen =
      new MenuItem(strings.format(CARDANT_STOCK_OPENINLOCATION));

    this.menuItemCopy.disableProperty()
      .bind(this.textField.selectedTextProperty().isEmpty());

    this.menuItemCopy.setOnAction(event -> this.textField.copy());
    this.menuItemSelectAll.setOnAction(event -> this.textField.selectAll());
    this.menuItemLocOpen.setOnAction(event -> {
      controller.locationGet(this.locationNow.id());
      controller.tabSelect(CAGTabKind.TAB_LOCATIONS);
    });

    this.customMenu =
      new ContextMenu(
        this.menuItemCopy,
        this.menuItemSelectAll,
        new SeparatorMenuItem(),
        this.menuItemLocOpen
      );

    this.textField.setContextMenu(this.customMenu);
  }

  @Override
  protected void updateItem(
    final CALocationSummary item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setText(null);
      this.setGraphic(null);
      return;
    }

    this.locationNow = item;
    this.setText(null);
    this.setPadding(Insets.EMPTY);

    this.textField.getStyleClass().setAll("stockTableTextField");
    this.textField.setBackground(Background.EMPTY);
    this.textField.setBorder(Border.EMPTY);
    this.textField.setText(item.name());
    this.textField.setEditable(false);
    this.setGraphic(this.textField);

    this.tooltip.setText(item.id().displayId());
  }
}
