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

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_FILES_SELECTTITLE;

/**
 * A file selection dialog.
 */

public final class CAGFileSelectDialogs
  extends CAGDialogFactoryAbstract<CAGFileSearchControllerType, CAGFileSelectView>
{
  /**
   * A file selection dialog.
   *
   * @param services The service directory
   */

  public CAGFileSelectDialogs(
    final RPServiceDirectoryType services)
  {
    super(
      CAGFileSelectView.class,
      "/com/io7m/cardant_gui/ui/internal/fileSelect.fxml",
      services,
      Modality.NONE
    );
  }

  @Override
  protected String createStageTitle(
    final CAGFileSearchControllerType arguments)
  {
    Objects.requireNonNull(arguments, "arguments");

    return this.strings().format(CARDANT_FILES_SELECTTITLE);
  }

  @Override
  protected CAGControllerFactoryType<CAGViewType> controllerFactory(
    final CAGFileSearchControllerType arguments,
    final Stage stage)
  {
    return CAGControllerFactoryMapped.create(
      Map.entry(
        CAGFileSelectView.class,
        () -> new CAGFileSelectView(stage, arguments)
      ),
      Map.entry(
        CAGFileListView.class,
        () -> new CAGFileListView(this.services())
      ),
      Map.entry(
        CAGFileSearchView.class,
        () -> new CAGFileSearchView(this.services())
      )
    );
  }

  @Override
  public String description()
  {
    return "File selection dialogs.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGFileSearchDialogs 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
