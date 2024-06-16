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
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Objects;

/**
 * An item details controller.
 */

public final class CAGItemDetailsController
  implements CAGItemDetailsControllerType
{
  private final CAGClientServiceType client;
  private final CAGItemModelType itemSelected;
  private final ObservableList<CAItemSummary> items;
  private final SortedList<CAItemSummary> itemsSorted;
  private final ObservableList<CAItemSummary> itemsRead;

  /**
   * Create a controller.
   *
   * @param clients The client service
   *
   * @return A controller
   */

  public static CAGItemDetailsControllerType create(
    final CAGClientServiceType clients)
  {
    final var controller = new CAGItemDetailsController(clients);
    clients.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.items.clear();
    this.itemSelected.clear();
  }

  private CAGItemDetailsController(
    final CAGClientServiceType inClientService)
  {
    this.client =
      Objects.requireNonNull(inClientService, "inClientService");

    this.itemSelected =
      CAGItemModel.create();
    this.items =
      FXCollections.observableArrayList();
    this.itemsSorted =
      new SortedList<>(this.items);
    this.itemsRead =
      FXCollections.unmodifiableObservableList(this.items);
  }

  @Override
  public ObservableList<CAItemSummary> itemsView()
  {
    return this.itemsRead;
  }

  @Override
  public SortedList<CAItemSummary> itemsViewSorted()
  {
    return this.itemsSorted;
  }

  @Override
  public void itemSelect(
    final CAItemID id)
  {
    final var future =
      this.client.execute(new CAICommandItemGet(id));

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        this.itemSelected.update(response.data());
      });
    });
  }

  @Override
  public void itemSelectNothing()
  {
    this.itemSelected.clear();
  }

  @Override
  public void itemAttachmentAdd(
    final CAICommandItemAttachmentAdd command)
  {
    this.client.execute(command);
    this.itemSelect(command.item());
  }

  @Override
  public CAGItemModelReadableType itemSelected()
  {
    return this.itemSelected;
  }
}
