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

import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A stock search controller.
 */

public final class CAGStockSearchController
  implements CAGStockSearchControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGStockSearchController.class);

  private final ObservableList<CAStockOccurrenceType> stockRead;
  private final SortedList<CAStockOccurrenceType> stockSorted;
  private final ObservableList<CAStockOccurrenceType> stock;
  private final SimpleObjectProperty<CAGPageRange> stockPages;
  private final CAGClientServiceType client;

  private CAGStockSearchController(
    final CAGClientServiceType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");

    this.stock =
      FXCollections.observableArrayList();
    this.stockSorted =
      new SortedList<>(this.stock);
    this.stockRead =
      FXCollections.unmodifiableObservableList(this.stock);

    this.stockPages =
      new SimpleObjectProperty<>(CAGPageRange.zero());
  }

  /**
   * @param client The client
   *
   * @return A stock search controller.
   */

  public static CAGStockSearchControllerType create(
    final CAGClientServiceType client)
  {
    final var controller = new CAGStockSearchController(client);
    client.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.stock.clear();
    this.stockPages.set(CAGPageRange.zero());
  }

  @Override
  public ObservableList<CAStockOccurrenceType> stockView()
  {
    return this.stockRead;
  }

  @Override
  public SortedList<CAStockOccurrenceType> stockViewSorted()
  {
    return this.stockSorted;
  }

  @Override
  public void stockSearchBegin(
    final CAStockSearchParameters searchParameters)
  {
    final var future =
      this.client.execute(
        new CAICommandStockSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data =
          response.data();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} stock", newItemPage.size());
        this.stockPages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.stock.setAll(newItemPage);
      });
    });
  }
}
