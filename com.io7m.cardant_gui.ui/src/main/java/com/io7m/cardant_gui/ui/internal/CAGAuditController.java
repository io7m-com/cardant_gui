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

import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAIResponseAuditSearch;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The audit controller.
 */

public final class CAGAuditController implements CAGAuditControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGAuditController.class);

  private final CAGClientServiceType clientService;
  private final ObservableList<CAAuditEvent> auditEvents;
  private final SimpleObjectProperty<CAGPageRange> auditEventPages;
  private final SortedList<CAAuditEvent> auditEventsSorted;

  private CAGAuditController(
    final CAGClientServiceType inClientService)
  {
    this.clientService =
      Objects.requireNonNull(inClientService, "clientService");

    this.auditEventPages =
      new SimpleObjectProperty<>(CAGPageRange.zero());
    this.auditEvents =
      FXCollections.observableArrayList();
    this.auditEventsSorted =
      new SortedList<>(this.auditEvents);
  }

  /**
   * Create a controller.
   *
   * @param clients The client service
   *
   * @return A controller
   */

  public static CAGAuditControllerType create(
    final CAGClientServiceType clients)
  {
    final var controller = new CAGAuditController(clients);
    clients.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.auditEvents.clear();
  }

  @Override
  public void auditSearchBegin(
    final CAAuditSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandAuditSearchBegin(searchParameters)
      );

    future.thenAccept(this::receivePage);
  }

  @Override
  public ObservableList<CAAuditEvent> auditEventsView()
  {
    return this.auditEvents;
  }

  @Override
  public SortedList<CAAuditEvent> auditEventsViewSorted()
  {
    return this.auditEventsSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> auditEventsPages()
  {
    return this.auditEventPages;
  }

  @Override
  public void auditSearchNext()
  {
    final var future =
      this.clientService.execute(new CAICommandAuditSearchNext());

    future.thenAccept(this::receivePage);
  }

  @Override
  public void auditSearchPrevious()
  {
    final var future =
      this.clientService.execute(new CAICommandAuditSearchPrevious());

    future.thenAccept(this::receivePage);
  }

  private void receivePage(
    final CAIResponseAuditSearch response)
  {
    Platform.runLater(() -> {
      final var data = response.results();

      final var newItemPage =
        new ArrayList<>(data.items());

      LOG.debug("Received {} audit events", newItemPage.size());
      this.auditEventPages.set(
        new CAGPageRange(
          (long) data.pageIndex(),
          (long) data.pageCount()
        )
      );
      this.auditEvents.setAll(newItemPage);
    });
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGAuditController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
