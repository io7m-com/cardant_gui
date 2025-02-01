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

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_STOCKADD_TITLE;

/**
 * A stock addition dialog.
 */

public final class CAGStockAddDialogs
  extends CAGDialogFactoryAbstract<CAGStockAddDialogArguments, CAGStockAddView>
{
  /**
   * A stock addition dialog.
   *
   * @param services The service directory
   */

  public CAGStockAddDialogs(
    final RPServiceDirectoryType services)
  {
    super(
      CAGStockAddView.class,
      "/com/io7m/cardant_gui/ui/internal/stockAdd.fxml",
      services,
      Modality.NONE
    );
  }

  @Override
  protected String createStageTitle(
    final CAGStockAddDialogArguments arguments)
  {
    Objects.requireNonNull(arguments, "arguments");

    return this.strings().format(CARDANT_STOCKADD_TITLE);
  }

  @Override
  protected CAGControllerFactoryType<CAGViewType> controllerFactory(
    final CAGStockAddDialogArguments arguments,
    final Stage stage)
  {
    Objects.requireNonNull(arguments, "arguments");
    Objects.requireNonNull(stage, "stage");

    return CAGControllerFactoryMapped.create(
      this.getClass(),
      Map.entry(
        CAGStockAddView.class,
        () -> new CAGStockAddView(stage, this.services(), arguments)
      )
    );
  }

  @Override
  public String description()
  {
    return "Stock add dialogs.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGStockAddDialogs 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
