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
 * The main type packages view.
 */

public final class CAGMainTypePackagesView implements CAGViewType
{
  private final CAGClientServiceType client;
  private final CAGTypePackagesControllerType typePackagesController;

  @FXML private Node typePackagesTable;
  @FXML private CAGTypePackagesTableView typePackagesTableController;
  @FXML private Node typePackagesSearch;
  @FXML private CAGTypePackagesSearchView typePackagesSearchController;
  @FXML private Node typePackagesDetails;
  @FXML private CAGTypePackagesDetailsView typePackagesDetailsController;

  /**
   * The main typePackagess view.
   *
   * @param services The application services.
   */

  public CAGMainTypePackagesView(
    final RPServiceDirectoryType services)
  {
    Objects.requireNonNull(services, "services");

    this.client =
      services.requireService(CAGClientServiceType.class);
    this.typePackagesController =
      CAGTypePackagesController.create(this.client);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.typePackagesTableController
      .setControllers(this.typePackagesController);
    this.typePackagesSearchController
      .setControllers(this.typePackagesController);
    this.typePackagesDetailsController
      .setControllers(this.typePackagesController);
  }
}
