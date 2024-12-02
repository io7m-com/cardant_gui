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

import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ATTACHMENTADD_TITLE;

/**
 * An item name setting dialog.
 */

public final class CAGItemNameSetDialogs
  extends CAGDialogFactoryAbstract<CAGItemNameSetDialogArguments, CAGItemNameSetView>
{
  /**
   * An item name setting dialog.
   *
   * @param services The service directory
   */

  public CAGItemNameSetDialogs(
    final RPServiceDirectoryType services)
  {
    super(
      CAGItemNameSetView.class,
      "/com/io7m/cardant_gui/ui/internal/itemSetName.fxml",
      services,
      Modality.NONE
    );
  }

  @Override
  protected String createStageTitle(
    final CAGItemNameSetDialogArguments arguments)
  {
    Objects.requireNonNull(arguments, "arguments");

    return this.strings().format(CARDANT_ATTACHMENTADD_TITLE);
  }

  @Override
  protected CAGControllerFactoryType<CAGViewType> controllerFactory(
    final CAGItemNameSetDialogArguments arguments,
    final Stage stage)
  {
    Objects.requireNonNull(arguments, "arguments");
    Objects.requireNonNull(stage, "stage");

    return CAGControllerFactoryMapped.create(
      Map.entry(
        CAGItemNameSetView.class,
        () -> new CAGItemNameSetView(stage, arguments)
      )
    );
  }

  @Override
  public String description()
  {
    return "Item name set dialogs.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGItemNameSetDialogs 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
