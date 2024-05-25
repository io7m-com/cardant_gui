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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A cell controller.
 */

public final class CAGItemAttachmentCellController
  implements Initializable
{
  private final CAGStringsType strings;

  @FXML private TextField id;
  @FXML private TextField relation;
  @FXML private TextField description;
  @FXML private TextField mediaType;
  @FXML private TextField size;
  @FXML private TextField hashAlgorithm;
  @FXML private TextField hashValue;

  /**
   * Construct a cell controller.
   *
   * @param inStrings      The string resources
   */

  public CAGItemAttachmentCellController(
    final CAGStringsType inStrings)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
  }

  void unsetItem()
  {
    this.description.setText("");
    this.hashAlgorithm.setText("");
    this.hashValue.setText("");
    this.id.setText("");
    this.mediaType.setText("");
    this.relation.setText("");
    this.size.setText("");
  }

  void setItem(
    final CAAttachment item)
  {
    final var file = item.file();
    this.description.setText(file.description());
    this.hashAlgorithm.setText(file.hashAlgorithm());
    this.hashValue.setText(file.hashValue());
    this.id.setText(item.key().fileID().displayId());
    this.mediaType.setText(file.mediaType());
    this.relation.setText(item.relation());
    this.size.setText(Long.toUnsignedString(file.size()));
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
