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

/**
 * An attachment addition dialog.
 */

public final class CAGAttachmentAddDialogs
  extends CAGDialogFactoryAbstract<Void, CAGAttachmentAddView>
{
  /**
   * An attachment addition dialog.
   *
   * @param services The service directory
   */

  public CAGAttachmentAddDialogs(
    final RPServiceDirectoryType services)
  {
    super(
      CAGAttachmentAddView.class,
      "/com/io7m/cardant_gui/ui/internal/attachmentAdd.fxml",
      services,
      Modality.NONE
    );
  }

  @Override
  protected String createStageTitle(
    final Void arguments)
  {
    return this.strings().format(CAGStringConstants.CARDANT_ATTACHMENTADD_TITLE);
  }

  @Override
  protected CAGAttachmentAddView createController(
    final Void arguments,
    final Stage stage)
  {
    return new CAGAttachmentAddView(stage, this.services());
  }

  @Override
  public String description()
  {
    return "Attachment add dialogs.";
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGAttachmentAddDialogs 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
