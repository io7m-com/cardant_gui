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

import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.lanark.core.RDottedName;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The stock serial addition view.
 */

public final class CAGStockSerialAddView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStockSearchControllerType stock;
  private final CAStockInstanceID instance;

  @FXML private Parent root;
  @FXML private TextField typeField;
  @FXML private TextField valueField;
  @FXML private Button addButton;

  /**
   * The stock addition view.
   *
   * @param inStage   The stage
   * @param arguments The arguments
   */

  public CAGStockSerialAddView(
    final Stage inStage,
    final CAGStockSerialAddDialogArguments arguments)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.stock =
      arguments.stockController();
    this.instance =
      arguments.stockInstance();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.addButton.setDisable(true);

    this.typeField.textProperty()
      .addListener(_ -> this.validate());
    this.valueField.textProperty()
      .addListener(_ -> this.validate());
  }

  private void validate()
  {
    var ok = true;

    try {
      new RDottedName(this.typeField.getText().trim());
    } catch (final Exception e) {
      ok = false;
    }

    ok = ok && !this.valueField.getText().isBlank();
    this.addButton.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onStockSerialAddSelected()
  {
    this.root.setDisable(true);

    final var future =
      this.stock.stockSerialAdd(
        this.instance,
        new CAItemSerial(
          new RDottedName(this.typeField.getText().trim()),
          this.valueField.getText().trim()
        )
      );

    future.whenComplete((_, exception) -> {
      Platform.runLater(() -> this.root.setDisable(false));

      if (exception == null) {
        Platform.runLater(this.stage::close);
      }
    });
  }
}
