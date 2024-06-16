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

import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A location tree controller.
 */

public final class CAGLocationTreeController
  implements CAGLocationTreeControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGLocationTreeController.class);

  private static final CALocationID ROOT_LOCATION =
    CALocationID.of("00000000-0000-0000-0000-000000000000");

  private static final CALocationSummary ROOT_LOCATION_SUMMARY =
    new CALocationSummary(ROOT_LOCATION, Optional.empty(), "Everywhere");

  private final ObservableList<CALocationSummary> locationsView;
  private final SimpleObjectProperty<CAGPageRange> locationPages;
  private final SimpleObjectProperty<TreeItem<CALocationSummary>> locationTree;
  private final CAGClientServiceType client;
  private final CAGLocationModelType locationSelected;

  private CAGLocationTreeController(
    final CAGClientServiceType inClient)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.locationSelected =
      CAGLocationModel.create();
    this.locationsView =
      FXCollections.observableArrayList();
    this.locationTree =
      new SimpleObjectProperty<>();
    this.locationPages =
      new SimpleObjectProperty<>(CAGPageRange.zero());
  }

  /**
   * @param client The client
   *
   * @return A location tree controller.
   */

  public static CAGLocationTreeControllerType create(
    final CAGClientServiceType client)
  {
    final var controller = new CAGLocationTreeController(client);
    client.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.locationsView.clear();
    this.locationPages.set(CAGPageRange.zero());
  }

  @Override
  public ObservableValue<TreeItem<CALocationSummary>> locationTree()
  {
    return this.locationTree;
  }

  @Override
  public void locationSearchBegin()
  {
    final var future =
      this.client.execute(new CAICommandLocationList());

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data =
          response.data();
        final var summaries =
          data.locations();

        LOG.debug("Received {} locations", summaries.size());

        final var treeItems =
          new HashMap<CALocationID, TreeItem<CALocationSummary>>(summaries.size());
        final var newRoot =
          new TreeItem<>(ROOT_LOCATION_SUMMARY);

        for (final var location : summaries.values()) {
          final var item = new TreeItem<>(location);
          treeItems.put(location.id(), item);
        }

        for (final var location : summaries.values()) {
          final var locationItem =
            treeItems.get(location.id());
          final var parent =
            location.parent();

          if (parent.isEmpty()) {
            newRoot.getChildren().add(locationItem);
            continue;
          }

          final var parentId =
            parent.get();
          final var parentItem =
            treeItems.get(parentId);

          if (parentItem == null) {
            LOG.warn(
              "Location {} provided a nonexistent parent {}",
              location.id(),
              parentId);
            continue;
          }

          parentItem.getChildren().add(locationItem);
        }

        this.locationTree.set(newRoot);
      });
    });
  }

  @Override
  public void locationRemove(
    final CALocationID location)
  {
    if (Objects.equals(location, ROOT_LOCATION)) {
      return;
    }

    final var future =
      this.client.execute(new CAICommandLocationDelete(location));

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        this.locationTreeDelete(this.locationTree.get(), location);
      });
    });
  }

  @Override
  public void locationCreate(
    final String name)
  {
    final var future =
      this.client.execute(new CAICommandLocationPut(
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          name,
          new TreeMap<>(),
          new TreeMap<>(),
          new TreeSet<>()
        )
      ));

    future.thenAccept(response -> this.locationSearchBegin());
  }

  @Override
  public void locationReparent(
    final CALocationID location,
    final CALocationID newParent)
  {
    final var future0 =
      this.client.execute(new CAICommandLocationGet(location));

    final var future1 =
      future0.thenCompose(response -> {
        final var source = response.data();
        return this.client.execute(
          new CAICommandLocationPut(
            new CALocation(
              source.id(),
              Optional.of(newParent),
              source.name(),
              source.metadata(),
              source.attachments(),
              source.types()
            )
          )
        );
      });

    future1.thenAccept(response -> this.locationSearchBegin());
  }

  @Override
  public void locationSelect(
    final CALocationID id)
  {
    if (Objects.equals(id, ROOT_LOCATION)) {
      return;
    }

    final var future =
      this.client.execute(new CAICommandLocationGet(id));

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        this.locationSelected.update(response.data());
      });
    });
  }

  @Override
  public void locationSelectNothing()
  {
    this.locationSelected.clear();
  }

  @Override
  public void locationAttachmentAdd(
    final CAICommandLocationAttachmentAdd command)
  {
    this.client.execute(command);
    this.locationSelect(command.location());
  }

  @Override
  public CAGLocationModelReadableType locationSelected()
  {
    return this.locationSelected;
  }

  private boolean locationTreeDelete(
    final TreeItem<CALocationSummary> tree,
    final CALocationID id)
  {
    if (Objects.equals(tree.getValue().id(), id)) {
      tree.getParent()
        .getChildren()
        .remove(tree);
      return true;
    }
    for (final var c : tree.getChildren()) {
      if (this.locationTreeDelete(c, id)) {
        return true;
      }
    }
    return false;
  }
}
