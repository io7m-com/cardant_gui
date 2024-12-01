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
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CAStockSearchParameters;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Stock methods for the controller.
 */

public interface CAGStockSearchControllerType
{
  /**
   * @return The stock for the current search query
   */

  ObservableList<CAStockOccurrenceType> stockView();

  /**
   * @return The stock for the current search query
   */

  SortedList<CAStockOccurrenceType> stockViewSorted();

  /**
   * Start searching for stock.
   *
   * @param searchParameters The search parameters
   */

  void stockSearchBegin(
    CAStockSearchParameters searchParameters);

  /**
   * Move the given serial stock occurrence to the given location.
   *
   * @param serial   The stock
   * @param location The target location
   */

  void stockSerialMove(
    CAStockOccurrenceSerial serial,
    CALocationID location
  );

  /**
   * Introduce a stock set.
   *
   * @param instance The instance
   * @param location The location
   * @param item     The item
   * @param count    The count
   */

  void stockSetIntroduce(
    CAStockInstanceID instance,
    CALocationID location,
    CAItemID item,
    long count
  );

  /**
   * Introduce a stock serial.
   *
   * @param instance The instance
   * @param location The location
   * @param item     The item
   * @param serial   The serial
   */

  void stockSerialIntroduce(
    CAStockInstanceID instance,
    CALocationID location,
    CAItemID item,
    CAItemSerial serial
  );

  /**
   * Add a serial number to a stock instance.
   *
   * @param instance The stock instance
   * @param serial   The serial
   */

  void stockSerialAdd(
    CAStockInstanceID instance,
    CAItemSerial serial
  );

  /**
   * Remove a serial number from a stock instance.
   *
   * @param instance The stock instance
   * @param serial   The serial
   */

  void stockSerialRemove(
    CAStockInstanceID instance,
    CAItemSerial serial
  );
}
