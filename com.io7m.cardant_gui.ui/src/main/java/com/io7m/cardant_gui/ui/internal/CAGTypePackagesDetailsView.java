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

import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The type package details view.
 */

public final class CAGTypePackagesDetailsView
  implements CAGViewType
{
  private final CAGStringsType strings;

  @FXML private TextArea text;
  @FXML private ListView<CATypeRecordIdentifier> types;
  @FXML private ListView<CATypeField> fields;

  private CAGTypePackagesControllerType controller;

  /**
   * The package details view.
   *
   * @param services The service directory
   */

  public CAGTypePackagesDetailsView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
  }

  /**
   * Set the controllers.
   *
   * @param c The controller
   */

  public void setControllers(
    final CAGTypePackagesControllerType c)
  {
    this.controller =
      Objects.requireNonNull(c, "c");

    this.text.textProperty()
      .bind(this.controller.typePackageTextSelected());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
