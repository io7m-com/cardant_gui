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
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialNumberAdd;
import com.io7m.cardant.model.CAStockRepositSerialNumberRemove;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseStockReposit;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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

  @Override
  public void stockSerialMove(
    final CAStockOccurrenceSerial serial,
    final CALocationID location)
  {
    Objects.requireNonNull(serial, "serial");
    Objects.requireNonNull(location, "location");

    final var future =
      this.client.execute(
        new CAICommandStockReposit(
          new CAStockRepositSerialMove(serial.instance(), location)
        )
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> this.updateStockOccurrence(response.data()));
    });
  }

  private void updateStockOccurrence(
    final CAStockOccurrenceType newInstance)
  {
    for (int index = 0; index < this.stock.size(); ++index) {
      final var oldInstance = this.stock.get(index);
      if (Objects.equals(oldInstance.instance(), newInstance.instance())) {
        this.stock.set(index, newInstance);
      }
    }
  }

  @Override
  public CompletableFuture<CAGUnit> stockSetIntroduce(
    final CAStockInstanceID instance,
    final CALocationID location,
    final CAItemID item,
    final long count)
  {
    Objects.requireNonNull(instance, "instance");
    Objects.requireNonNull(location, "location");
    Objects.requireNonNull(item, "item");

    return this.client.execute(
      new CAICommandStockReposit(
        new CAStockRepositSetIntroduce(
          instance,
          item,
          location,
          count
        )
      )
    ).thenApply(_ -> CAGUnit.UNIT);
  }

  @Override
  public CompletableFuture<CAGUnit> stockSerialIntroduce(
    final CAStockInstanceID instance,
    final CALocationID location,
    final CAItemID item,
    final CAItemSerial serial)
  {
    Objects.requireNonNull(instance, "instance");
    Objects.requireNonNull(location, "location");
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(serial, "serial");

    return this.client.execute(
      new CAICommandStockReposit(
        new CAStockRepositSerialIntroduce(
          instance,
          item,
          location,
          serial
        )
      )
    ).thenApply(_ -> CAGUnit.UNIT);
  }

  @Override
  public CompletableFuture<CAStockOccurrenceType> stockSerialAdd(
    final CAStockInstanceID instance,
    final CAItemSerial serial)
  {
    Objects.requireNonNull(instance, "instance");
    Objects.requireNonNull(serial, "serial");

    final var future =
      this.client.execute(
        new CAICommandStockReposit(
          new CAStockRepositSerialNumberAdd(
            instance,
            serial
          )
        )
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> this.updateStockOccurrence(response.data()));
    });

    return future.thenApply(CAIResponseStockReposit::data);
  }

  @Override
  public CompletableFuture<CAStockOccurrenceType> stockSerialRemove(
    final CAStockInstanceID instance,
    final CAItemSerial serial)
  {
    Objects.requireNonNull(instance, "instance");
    Objects.requireNonNull(serial, "serial");

    final var future =
      this.client.execute(
        new CAICommandStockReposit(
          new CAStockRepositSerialNumberRemove(
            instance,
            serial
          )
        )
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> this.updateStockOccurrence(response.data()));
    });

    return future.thenApply(CAIResponseStockReposit::data);
  }
}
