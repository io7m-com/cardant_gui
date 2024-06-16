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
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The main items view.
 */

public final class CAGMainItemsView implements CAGViewType
{
  private final CAGClientServiceType client;

  @FXML private Node itemTableView;
  @FXML private CAGItemTableView itemTableViewController;
  @FXML private Node itemSearchView;
  @FXML private CAGItemSearchView itemSearchViewController;
  @FXML private Node itemDetailsView;
  @FXML private CAGItemDetailsView itemDetailsViewController;

  private final CAGItemDetailsControllerType itemDetailsController;
  private final CAGItemSearchControllerType itemSearchController;

  /**
   * The main items view.
   *
   * @param services The application services.
   */

  public CAGMainItemsView(
    final RPServiceDirectoryType services)
  {
    Objects.requireNonNull(services, "services");

    this.client =
      services.requireService(CAGClientServiceType.class);
    this.itemDetailsController =
      CAGItemDetailsController.create(this.client);
    this.itemSearchController =
      CAGItemSearchController.create(this.client);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.itemTableViewController
      .setControllers(
        this.itemSearchController,
        this.itemDetailsController
      );
    this.itemSearchViewController
      .setControllers(this.itemSearchController);
    this.itemDetailsViewController
      .setControllers(this.itemDetailsController);
  }
}
