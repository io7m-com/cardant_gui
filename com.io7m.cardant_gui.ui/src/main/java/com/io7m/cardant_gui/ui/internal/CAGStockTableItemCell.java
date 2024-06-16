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
import com.io7m.cardant.model.CAStockOccurrenceType;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_COPY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SELECTALL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCK_OPENINITEMS;

/**
 * A cell displaying a stock item.
 */

public final class CAGStockTableItemCell
  extends TableCell<CAStockOccurrenceType, CAItemID>
{
  private final TextField textField = new TextField();
  private final MenuItem menuItemCopy;
  private final MenuItem menuItemSelectAll;
  private final MenuItem menuItemOpen;
  private final ContextMenu customMenu;
  private CAItemID itemNow;

  /**
   * A cell displaying a stock item.
   *
   * @param strings The string resources
   */

  public CAGStockTableItemCell(
    final CAGStringsType strings)
  {
    this.menuItemCopy =
      new MenuItem(strings.format(CARDANT_COPY));
    this.menuItemSelectAll =
      new MenuItem(strings.format(CARDANT_SELECTALL));
    this.menuItemOpen =
      new MenuItem(strings.format(CARDANT_STOCK_OPENINITEMS));

    this.menuItemCopy.disableProperty()
      .bind(this.textField.selectedTextProperty().isEmpty());

    this.menuItemCopy.setOnAction(event -> this.textField.copy());
    this.menuItemSelectAll.setOnAction(event -> this.textField.selectAll());
    this.menuItemOpen.setOnAction(event -> {

    });

    this.customMenu =
      new ContextMenu(
        this.menuItemCopy,
        this.menuItemSelectAll,
        new SeparatorMenuItem(),
        this.menuItemOpen
      );

    this.textField.setContextMenu(this.customMenu);
  }

  @Override
  protected void updateItem(
    final CAItemID item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setText(null);
      this.setGraphic(null);
      return;
    }

    this.itemNow = item;
    this.setText(null);
    this.setPadding(Insets.EMPTY);

    this.textField.getStyleClass().setAll("stockTableTextField");
    this.textField.setBackground(Background.EMPTY);
    this.textField.setBorder(Border.EMPTY);
    this.textField.setText(item.displayId());
    this.textField.setEditable(false);
    this.setGraphic(this.textField);
  }
}
