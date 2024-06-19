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

import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Objects;

/**
 * An item search controller.
 */

public final class CAGItemSearchController
  extends CAGAbstractResourceHolder
  implements CAGItemSearchControllerType
{
  private final ObservableList<CAItemSummary> itemsView;
  private final SortedList<CAItemSummary> itemsViewSorted;
  private final SimpleObjectProperty<CAGPageRange> itemPages;
  private final CAGClientServiceType client;

  private CAGItemSearchController(
    final CAGClientServiceType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.itemsView =
      FXCollections.observableArrayList();
    this.itemsViewSorted =
      new SortedList<>(this.itemsView);
    this.itemPages =
      new SimpleObjectProperty<>(CAGPageRange.zero());
  }

  /**
   * @param events The event service
   * @param client The client
   *
   * @return An item search controller.
   */

  public static CAGItemSearchControllerType create(
    final CAGEventServiceType events,
    final CAGClientServiceType client)
  {
    final var controller = new CAGItemSearchController(client);

    controller.trackResource(
      client.status()
        .subscribe((oldStatus, newStatus) -> controller.onClientStatusChanged())
    );

    final var subscriber =
      controller.trackResource(
        CAGCloseableSubscriber.create(controller::onEvent));

    events.events().subscribe(subscriber);
    return controller;
  }

  private void onEvent(
    final CAGEventType event)
  {
    switch (event) {
      case final CAGEventItemDeleted e -> {
        Platform.runLater(() -> {
          this.itemsView.removeIf(i -> Objects.equals(i.id(), e.item()));
        });
      }
      case final CAGEventItemUpdated e -> {
        Platform.runLater(() -> {
          this.itemsView.replaceAll(summary -> {
            if (Objects.equals(summary.id(), e.item().id())) {
              return e.item().summary();
            } else {
              return summary;
            }
          });
        });
      }
      case final CAGEventLocationUpdated e -> {
        // Nothing to do
      }
    }
  }

  private void onClientStatusChanged()
  {
    this.itemsView.clear();
    this.itemPages.set(CAGPageRange.zero());
  }

  @Override
  public void itemSearchBegin(
    final CAItemSearchParameters parameters)
  {
    this.client.execute(new CAICommandItemSearchBegin(parameters))
      .thenAccept(response -> {
        Platform.runLater(() -> {
          final var data = response.data();
          this.itemsView.setAll(data.items());
          this.itemPages.set(
            new CAGPageRange(
              (long) data.pageIndex(),
              (long) data.pageCount()
            )
          );
        });
      });
  }

  @Override
  public ObservableList<CAItemSummary> itemsView()
  {
    return FXCollections.unmodifiableObservableList(this.itemsView);
  }

  @Override
  public SortedList<CAItemSummary> itemsViewSorted()
  {
    return this.itemsViewSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> itemPages()
  {
    return this.itemPages;
  }
}
