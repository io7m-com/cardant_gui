/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant_gui.ui;

import com.io7m.cardant_gui.ui.internal.CAGAuditSearchView;
import com.io7m.cardant_gui.ui.internal.CAGAuditTableView;
import com.io7m.cardant_gui.ui.internal.CAGCSS;
import com.io7m.cardant_gui.ui.internal.CAGClientService;
import com.io7m.cardant_gui.ui.internal.CAGClientServiceType;
import com.io7m.cardant_gui.ui.internal.CAGControllerFactoryMapped;
import com.io7m.cardant_gui.ui.internal.CAGEventService;
import com.io7m.cardant_gui.ui.internal.CAGEventServiceType;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosers;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosersType;
import com.io7m.cardant_gui.ui.internal.CAGFileListView;
import com.io7m.cardant_gui.ui.internal.CAGFileSearchView;
import com.io7m.cardant_gui.ui.internal.CAGFileSelectDialogs;
import com.io7m.cardant_gui.ui.internal.CAGFileTransferController;
import com.io7m.cardant_gui.ui.internal.CAGFileTransferControllerType;
import com.io7m.cardant_gui.ui.internal.CAGFileViewDialogs;
import com.io7m.cardant_gui.ui.internal.CAGItemAttachmentAddDialogs;
import com.io7m.cardant_gui.ui.internal.CAGItemCreateDialogs;
import com.io7m.cardant_gui.ui.internal.CAGItemDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGItemSearchView;
import com.io7m.cardant_gui.ui.internal.CAGItemSelectDialogs;
import com.io7m.cardant_gui.ui.internal.CAGItemTableView;
import com.io7m.cardant_gui.ui.internal.CAGLocationAttachmentAddDialogs;
import com.io7m.cardant_gui.ui.internal.CAGLocationDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGLocationReparentDialogs;
import com.io7m.cardant_gui.ui.internal.CAGLocationSelectDialogs;
import com.io7m.cardant_gui.ui.internal.CAGLocationTreeView;
import com.io7m.cardant_gui.ui.internal.CAGMainAuditView;
import com.io7m.cardant_gui.ui.internal.CAGMainFilesView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemsView;
import com.io7m.cardant_gui.ui.internal.CAGMainLocationsView;
import com.io7m.cardant_gui.ui.internal.CAGMainStockView;
import com.io7m.cardant_gui.ui.internal.CAGMainTypePackagesView;
import com.io7m.cardant_gui.ui.internal.CAGMainView;
import com.io7m.cardant_gui.ui.internal.CAGStatusService;
import com.io7m.cardant_gui.ui.internal.CAGStockSearchView;
import com.io7m.cardant_gui.ui.internal.CAGStockTableView;
import com.io7m.cardant_gui.ui.internal.CAGStringConstants;
import com.io7m.cardant_gui.ui.internal.CAGStrings;
import com.io7m.cardant_gui.ui.internal.CAGStringsType;
import com.io7m.cardant_gui.ui.internal.CAGTypePackagesDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGTypePackagesSearchView;
import com.io7m.cardant_gui.ui.internal.CAGTypePackagesTableView;
import com.io7m.cardant_gui.ui.internal.CAGViewType;
import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseConfiguration;
import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseFactory;
import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseType;
import com.io7m.darco.api.DDatabaseCreate;
import com.io7m.darco.api.DDatabaseTelemetryNoOp;
import com.io7m.darco.api.DDatabaseUpgrade;
import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.repetoir.core.RPServiceDirectory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * The main application class responsible for starting up the "main" view.
 */

public final class CAGApplication extends Application
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGApplication.class);

  private final ApplicationDirectoriesType directories;

  /**
   * The main application class responsible for starting up the "main" view.
   *
   * @param inConfiguration The configuration
   */

  public CAGApplication(
    final ApplicationDirectoriesType inConfiguration)
  {
    this.directories =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  @Override
  public void start(
    final Stage stage)
    throws Exception
  {
    final var strings =
      new CAGStrings(Locale.getDefault());
    final var services =
      new RPServiceDirectory();

    final var database =
      new CAGDatabaseFactory()
        .open(
          new CAGDatabaseConfiguration(
            DDatabaseTelemetryNoOp.get(),
            DDatabaseCreate.CREATE_DATABASE,
            DDatabaseUpgrade.UPGRADE_DATABASE,
            this.directories.configurationDirectory()
              .resolve("database.db")
          ),
          event -> {

          }
        );
    services.register(
      CAGDatabaseType.class,
      database
    );

    final var events = CAGEventService.create();
    services.register(CAGEventServiceType.class, events);

    services.register(
      CAGFileChoosersType.class,
      new CAGFileChoosers(services)
    );
    services.register(
      CAGStringsType.class,
      strings
    );
    services.register(
      CAGFileViewDialogs.class,
      new CAGFileViewDialogs(services)
    );
    services.register(
      CAGLocationAttachmentAddDialogs.class,
      new CAGLocationAttachmentAddDialogs(services)
    );
    services.register(
      CAGLocationSelectDialogs.class,
      new CAGLocationSelectDialogs(services)
    );
    services.register(
      CAGItemAttachmentAddDialogs.class,
      new CAGItemAttachmentAddDialogs(services)
    );
    services.register(
      CAGLocationReparentDialogs.class,
      new CAGLocationReparentDialogs(services)
    );
    services.register(
      CAGFileSelectDialogs.class,
      new CAGFileSelectDialogs(services)
    );
    services.register(
      CAGItemSelectDialogs.class,
      new CAGItemSelectDialogs(services)
    );

    final var status = new CAGStatusService();
    services.register(CAGStatusService.class, status);

    final var clientService = new CAGClientService(status, events, strings);
    services.register(CAGClientServiceType.class, clientService);

    services.register(
      CAGFileTransferControllerType.class,
      CAGFileTransferController.create(clientService)
    );
    services.register(
      CAGItemCreateDialogs.class,
      new CAGItemCreateDialogs(services)
    );

    final var xml =
      CAGMainView.class.getResource(
        "/com/io7m/cardant_gui/ui/internal/main.fxml"
      );
    final var resources =
      strings.resources();
    final var loader =
      new FXMLLoader(xml, resources);

    final var controllers =
      CAGControllerFactoryMapped.create(
        Map.entry(
          CAGMainView.class,
          () -> new CAGMainView(services)
        ),
        Map.entry(
          CAGMainItemsView.class,
          () -> new CAGMainItemsView(services)
        ),
        Map.entry(
          CAGItemSearchView.class,
          () -> new CAGItemSearchView(services)
        ),
        Map.entry(
          CAGItemTableView.class,
          () -> new CAGItemTableView(services)
        ),
        Map.entry(
          CAGItemDetailsView.class,
          () -> new CAGItemDetailsView(services)
        ),
        Map.entry(
          CAGMainAuditView.class,
          () -> new CAGMainAuditView(services)
        ),
        Map.entry(
          CAGMainLocationsView.class,
          () -> new CAGMainLocationsView(services)
        ),
        Map.entry(
          CAGFileSearchView.class,
          () -> new CAGFileSearchView(services)
        ),
        Map.entry(
          CAGMainFilesView.class,
          () -> new CAGMainFilesView(services)
        ),
        Map.entry(
          CAGMainStockView.class,
          () -> new CAGMainStockView(services)
        ),
        Map.entry(
          CAGMainTypePackagesView.class,
          () -> new CAGMainTypePackagesView(services)
        ),
        Map.entry(
          CAGTypePackagesSearchView.class,
          () -> new CAGTypePackagesSearchView(services)
        ),
        Map.entry(
          CAGTypePackagesTableView.class,
          () -> new CAGTypePackagesTableView(services)
        ),
        Map.entry(
          CAGTypePackagesDetailsView.class,
          () -> new CAGTypePackagesDetailsView(services)
        ),
        Map.entry(
          CAGAuditSearchView.class,
          () -> new CAGAuditSearchView(services)
        ),
        Map.entry(
          CAGAuditTableView.class,
          () -> new CAGAuditTableView(services)
        ),
        Map.entry(
          CAGFileListView.class,
          () -> new CAGFileListView(services)
        ),
        Map.entry(
          CAGStockSearchView.class,
          () -> new CAGStockSearchView(services)
        ),
        Map.entry(
          CAGStockTableView.class,
          () -> new CAGStockTableView(services)
        ),
        Map.entry(
          CAGLocationTreeView.class,
          () -> new CAGLocationTreeView(services)
        ),
        Map.entry(
          CAGLocationDetailsView.class,
          () -> new CAGLocationDetailsView(services)
        )
      );

    loader.setControllerFactory(param -> {
      return controllers.call((Class<? extends CAGViewType>) param);
    });

    final var pane = loader.<Pane>load();
    CAGCSS.setCSS(pane);
    stage.setScene(new Scene(pane));
    stage.setTitle(strings.format(CAGStringConstants.CARDANT_TITLE));
    stage.show();
  }
}
