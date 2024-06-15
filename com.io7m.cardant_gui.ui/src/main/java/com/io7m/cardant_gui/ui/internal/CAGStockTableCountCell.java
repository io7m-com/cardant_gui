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

import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

/**
 * A cell displaying a stock count.
 */

public final class CAGStockTableCountCell
  extends TableCell<CAStockOccurrenceType, CAStockOccurrenceType>
{
  private final TextField textField = new TextField();

  /**
   * A cell displaying a stock count.
   */

  public CAGStockTableCountCell()
  {

  }

  @Override
  protected void updateItem(
    final CAStockOccurrenceType item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setText(null);
      this.setGraphic(null);
      return;
    }

    this.setText(null);
    this.setPadding(Insets.EMPTY);

    this.textField.getStyleClass().setAll("stockTableTextField");
    this.textField.setAlignment(Pos.BASELINE_RIGHT);
    this.textField.setPadding(new Insets(0.0, 4.0, 0.0, 0.0));
    this.textField.setBackground(Background.EMPTY);
    this.textField.setBorder(Border.EMPTY);
    this.textField.setText(
      switch (item) {
        case final CAStockOccurrenceSerial ignored -> {
          yield "1";
        }
        case final CAStockOccurrenceSet set -> {
          yield Long.toUnsignedString(set.count());
        }
      });
    this.textField.setEditable(false);
    this.setGraphic(this.textField);
  }
}
