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

import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A cell controller.
 */

public final class CAGFileCellController
  implements Initializable
{
  private final CAGStringsType strings;

  @FXML private TextField idField;
  @FXML private TextArea descriptionField;
  @FXML private TextField mediaTypeField;
  @FXML private TextField sizeField;
  @FXML private TextField hashAlgoField;
  @FXML private TextField hashValueField;

  /**
   * Construct a cell controller.
   *
   * @param inStrings The string resources
   */

  public CAGFileCellController(
    final CAGStringsType inStrings)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
  }

  void unsetItem()
  {

  }

  void setItem(
    final CAFileWithoutData item)
  {
    this.idField.setText(item.id().id().toString());
    this.descriptionField.setText(item.description());
    this.mediaTypeField.setText(item.mediaType());
    this.sizeField.setText(Long.toUnsignedString(item.size()));
    this.hashAlgoField.setText(item.hashAlgorithm());
    this.hashValueField.setText(item.hashValue());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
