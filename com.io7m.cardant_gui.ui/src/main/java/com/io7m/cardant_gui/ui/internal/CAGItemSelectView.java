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

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * An item selection view.
 */

public final class CAGItemSelectView
  implements CAGViewType
{
  private final Stage stage;
  private final CAGItemSearchControllerType search;
  private final CAGItemDetailsControllerType details;

  @FXML private Node itemSearch;
  @FXML private CAGItemSearchView itemSearchController;
  @FXML private Node itemDetails;
  @FXML private CAGItemDetailsView itemDetailsController;
  @FXML private Node itemTable;
  @FXML private CAGItemTableView itemTableController;
  @FXML private Button select;

  /**
   * An item selection view.
   *
   * @param inStage      The stage
   * @param inSearch The search controller
   * @param inDetails The details controller
   */

  public CAGItemSelectView(
    final Stage inStage,
    final CAGItemDetailsControllerType inDetails,
    final CAGItemSearchControllerType inSearch)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.details =
      Objects.requireNonNull(inDetails, "inDetails");
    this.search =
      Objects.requireNonNull(inSearch, "inSearch");
  }

  @Override
  public void initialize(
    final URL item,
    final ResourceBundle resources)
  {
    this.itemSearchController
      .setControllers(this.search);
    this.itemDetailsController
      .setControllers(this.details);
    this.itemTableController
      .setControllers(this.search, this.details);

    this.details.itemSelected()
      .summary()
      .addListener((observable, oldValue, newValue) -> {
        this.select.setDisable(newValue.isEmpty());
      });
  }

  @FXML
  private void onCancelSelected()
  {
    this.details.itemSelectNothing();
    this.stage.close();
  }

  @FXML
  private void onSelectSelected()
  {
    this.stage.close();
  }
}
