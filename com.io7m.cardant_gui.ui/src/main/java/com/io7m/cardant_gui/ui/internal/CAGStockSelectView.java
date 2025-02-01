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

import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A stock selection view.
 */

public final class CAGStockSelectView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGStockSearchControllerType controller;
  private final CAGStockSelectRestriction selectRestriction;

  @FXML private Node stockList;
  @FXML private CAGStockTableView stockListController;
  @FXML private Node stockSearch;
  @FXML private CAGStockSearchView stockSearchController;
  @FXML private Button select;

  /**
   * A stock selection view.
   *
   * @param inStage             The stage
   * @param inController        The tree controller
   * @param inSelectRestriction The selection restriction
   */

  public CAGStockSelectView(
    final Stage inStage,
    final CAGStockSearchControllerType inController,
    final CAGStockSelectRestriction inSelectRestriction)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.controller =
      Objects.requireNonNull(inController, "inController");
    this.selectRestriction =
      Objects.requireNonNull(inSelectRestriction, "selectRestriction");
  }

  @Override
  public void initialize(
    final URL file,
    final ResourceBundle resources)
  {
    this.stockListController
      .setControllers(this.controller);
    this.stockSearchController
      .setControllers(this.controller);

    this.controller.stockSelected()
      .addListener((_, _, newValue) -> {
        this.validate();
      });

    this.validate();
  }

  private void validate()
  {
    var ok = false;

    final var stockOpt =
      this.controller.stockSelected().getValue();

    if (stockOpt.isPresent()) {
      final var stock = stockOpt.get();
      switch (this.selectRestriction) {
        case ALL_OCCURRENCES -> {
          ok = true;
        }
        case SET_OCCURRENCES -> {
          ok = stock instanceof CAStockOccurrenceSet;
        }
        case SERIAL_OCCURRENCES -> {
          ok = stock instanceof CAStockOccurrenceSerial;
        }
      }
    }

    this.select.setDisable(!ok);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onSelectSelected()
  {
    this.stage.close();
  }
}
