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

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An observable item.
 */

public final class CAGItemModel implements CAGItemModelType
{
  private final SimpleObjectProperty<Optional<CAItemSummary>> summary;
  private final ObservableList<CAMetadataType> metadata;
  private final SortedList<CAMetadataType> metadataSorted;
  private final ObservableList<CAAttachment> attachments;
  private final ObservableList<CATypeRecordIdentifier> types;
  private final SortedList<CATypeRecordIdentifier> typesSorted;
  private final ObservableList<CAAttachment> attachmentsRead;

  private CAGItemModel()
  {
    this.summary =
      new SimpleObjectProperty<>(Optional.empty());
    this.metadata =
      FXCollections.observableArrayList();
    this.metadataSorted =
      new SortedList<>(this.metadata);
    this.attachments =
      FXCollections.observableArrayList();
    this.attachmentsRead =
      FXCollections.unmodifiableObservableList(this.attachments);
    this.types =
      FXCollections.observableArrayList();
    this.typesSorted =
      new SortedList<>(this.types);
  }

  /**
   * @return An observable item
   */

  public static CAGItemModelType create()
  {
    return new CAGItemModel();
  }

  @Override
  public ObservableValue<Optional<CAItemSummary>> summary()
  {
    return this.summary;
  }

  @Override
  public SortedList<CAMetadataType> metadata()
  {
    return this.metadataSorted;
  }

  @Override
  public ObservableList<CAAttachment> attachments()
  {
    return this.attachmentsRead;
  }

  @Override
  public SortedList<CATypeRecordIdentifier> types()
  {
    return this.typesSorted;
  }

  @Override
  public void update(
    final CAItem item)
  {
    this.summary.set(Optional.of(item.summary()));

    this.metadata.setAll(
      item.metadata()
        .values()
        .stream()
        .toList()
    );

    this.attachments.setAll(
      item.attachments()
        .values()
        .stream()
        .sorted(Comparator.comparing(o -> o.key().fileID()))
        .collect(Collectors.toList())
    );

    this.types.setAll(item.types());
  }

  @Override
  public void clear()
  {
    this.summary.set(Optional.empty());
    this.metadata.clear();
    this.attachments.clear();
    this.types.clear();
  }
}
