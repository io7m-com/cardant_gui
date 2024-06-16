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

import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;
import java.util.Optional;

/**
 * A file search controller.
 */

public final class CAGFileSearchController
  implements CAGFileSearchControllerType
{
  private final ObservableList<CAFileWithoutData> files;
  private final ObservableList<CAFileWithoutData> filesRead;
  private final SimpleObjectProperty<CAGPageRange> pages;
  private final CAGClientServiceType client;
  private final SimpleObjectProperty<Optional<CAFileWithoutData>> fileSelected;

  private CAGFileSearchController(
    final CAGClientServiceType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.files =
      FXCollections.observableArrayList();
    this.filesRead =
      FXCollections.unmodifiableObservableList(this.files);
    this.fileSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.pages =
      new SimpleObjectProperty<>(CAGPageRange.zero());
  }

  /**
   * @param client The client
   *
   * @return A file search controller.
   */

  public static CAGFileSearchControllerType create(
    final CAGClientServiceType client)
  {
    final var controller = new CAGFileSearchController(client);
    client.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.files.clear();
    this.pages.set(CAGPageRange.zero());
  }

  @Override
  public void fileSearchBegin(
    final CAFileSearchParameters searchParameters)
  {
    this.client.execute(new CAICommandFileSearchBegin(searchParameters))
      .thenAccept(response -> {
        Platform.runLater(() -> {
          final var data = response.data();
          this.files.setAll(data.items());
          this.pages.set(new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          ));
        });
      });
  }

  @Override
  public ObservableList<CAFileWithoutData> filesView()
  {
    return this.filesRead;
  }

  @Override
  public ObservableValue<CAGPageRange> filePages()
  {
    return this.pages;
  }

  @Override
  public ObservableValue<Optional<CAFileWithoutData>> fileSelected()
  {
    return this.fileSelected;
  }

  @Override
  public void fileSelect(
    final CAFileWithoutData file)
  {
    this.fileSelected.set(Optional.of(file));
  }

  @Override
  public void fileSelectNone()
  {
    this.fileSelected.set(Optional.empty());
  }
}
