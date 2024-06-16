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

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOCATIONS_SELECTTITLE;

/**
 * A location selection dialog.
 */

public final class CAGLocationSelectDialogs
  extends CAGDialogFactoryAbstract<CAGLocationTreeControllerType, CAGLocationSelectView>
{
  /**
   * A location selection dialog.
   *
   * @param services The service directory
   */

  public CAGLocationSelectDialogs(
    final RPServiceDirectoryType services)
  {
    super(
      CAGLocationSelectView.class,
      "/com/io7m/cardant_gui/ui/internal/locationSelect.fxml",
      services,
      Modality.NONE
    );
  }

  @Override
  protected String createStageTitle(
    final CAGLocationTreeControllerType arguments)
  {
    Objects.requireNonNull(arguments, "arguments");

    return this.strings().format(CARDANT_LOCATIONS_SELECTTITLE);
  }

  protected CAGLocationSelectView createController(
    final CAGLocationTreeControllerType arguments,
    final Stage stage)
  {
    Objects.requireNonNull(arguments, "arguments");
    Objects.requireNonNull(stage, "stage");

    return new CAGLocationSelectView(stage, arguments);
  }

  @Override
  protected CAGControllerFactoryType<CAGViewType> controllerFactory(
    final CAGLocationTreeControllerType arguments,
    final Stage stage)
  {
    return CAGControllerFactoryMapped.create(
      Map.entry(
        CAGLocationTreeView.class,
        () -> new CAGLocationTreeView(this.services())
      ),
      Map.entry(
        CAGLocationSelectView.class,
        () -> new CAGLocationSelectView(stage, arguments)
      )
    );
  }

  @Override
  public String description()
  {
    return "Location select dialogs.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGLocationSelectDialogs 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
