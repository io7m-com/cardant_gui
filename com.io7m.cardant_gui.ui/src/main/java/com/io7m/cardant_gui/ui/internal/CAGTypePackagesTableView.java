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

import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseType;
import com.io7m.cardant_gui.ui.internal.database.CAGRecentFileAddType;
import com.io7m.darco.api.DDatabaseException;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.lanark.core.RDottedName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.verona.core.Version;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The table of type packages.
 */

public final class CAGTypePackagesTableView
  implements CAGViewType
{
  private CAGTypePackagesControllerType controller;
  private final CAGStringsType strings;
  private final CAGFileChoosersType choosers;
  private final CAGDatabaseType database;

  @FXML private TableView<CATypePackageSummary> typePackageTable;
  @FXML
  private TableColumn<CATypePackageSummary, RDottedName> typePackageTableName;
  @FXML
  private TableColumn<CATypePackageSummary, Version> typePackageTableVersion;
  @FXML
  private TableColumn<CATypePackageSummary, String> typePackageTableDescription;
  @FXML private Label resultsLabel;
  @FXML private Button typePackageAdd;
  @FXML private Button typePackageRemove;

  /**
   * The table of type packages.
   *
   * @param services The service directory
   */

  public CAGTypePackagesTableView(
    final RPServiceDirectoryType services)
  {
    Objects.requireNonNull(services, "services");

    this.strings =
      services.requireService(CAGStringsType.class);
    this.choosers =
      services.requireService(CAGFileChoosersType.class);
    this.database =
      services.requireService(CAGDatabaseType.class);
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

    this.typePackageTable.setItems(
      this.controller.typePackagesViewSorted());

    this.controller.typePackagesViewSorted()
      .comparatorProperty()
      .bind(this.typePackageTable.comparatorProperty());

  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.typePackageTable.setPlaceholder(new Label(""));

    this.typePackageTableName.setSortable(true);
    this.typePackageTableName.setReorderable(false);
    this.typePackageTableName.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().identifier().name());
    });

    this.typePackageTableVersion.setSortable(true);
    this.typePackageTableVersion.setReorderable(false);
    this.typePackageTableVersion.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().identifier().version());
    });

    this.typePackageTableDescription.setSortable(true);
    this.typePackageTableDescription.setReorderable(false);
    this.typePackageTableDescription.setCellValueFactory(param -> {
      return new SimpleObjectProperty<>(param.getValue().description());
    });

    this.typePackageTable.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CATypePackageSummary>) this::onTypePackageSelectionChanged);
  }

  private void onTypePackageSelectionChanged(
    final ListChangeListener.Change<? extends CATypePackageSummary> c)
  {
    this.typePackageRemove.setDisable(true);

    final var selected = c.getList();
    if (selected.isEmpty()) {
      this.controller.typePackageSelectNothing();
      return;
    }

    if (selected.size() == 1) {
      final var selectedItem = selected.get(0);
      this.typePackageRemove.setDisable(false);
      this.controller.typePackageGet(selectedItem.identifier());
      return;
    }
  }

  @FXML
  private void onTypePackageAddSelected()
  {
    final var configuration =
      JWFileChooserConfiguration.builder()
        .setAction(JWFileChooserAction.OPEN_EXISTING_SINGLE)
        .build();

    final var chooser =
      this.choosers.create(configuration);
    final var results =
      chooser.showAndWait();

    if (results.isEmpty()) {
      return;
    }

    final var file = results.get(0);
    this.choosers.setMostRecentDirectory(file);
    this.addRecentFile(file);
    this.controller.typePackageInstall(file);
  }

  private void addRecentFile(
    final Path file)
  {
    try (var t = this.database.openTransaction()) {
      t.query(CAGRecentFileAddType.class).execute(file);
      t.commit();
    } catch (final DDatabaseException e) {
      // Nothing can be done.
    }
  }

  @FXML
  private void onTypePackageRemoveSelected()
  {

  }

  @FXML
  private void onPagePreviousSelected()
  {

  }

  @FXML
  private void onPageNextSelected()
  {

  }
}
