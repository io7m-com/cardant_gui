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

import com.io7m.cardant_gui.ui.internal.CAGCSS;
import com.io7m.cardant_gui.ui.internal.CAGClientService;
import com.io7m.cardant_gui.ui.internal.CAGClientServiceType;
import com.io7m.cardant_gui.ui.internal.CAGController;
import com.io7m.cardant_gui.ui.internal.CAGControllerType;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosers;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosersType;
import com.io7m.cardant_gui.ui.internal.CAGFileViewDialogs;
import com.io7m.cardant_gui.ui.internal.CAGItemAttachmentAddDialogs;
import com.io7m.cardant_gui.ui.internal.CAGLocationAttachmentAddDialogs;
import com.io7m.cardant_gui.ui.internal.CAGLocationReparentDialogs;
import com.io7m.cardant_gui.ui.internal.CAGMainAuditSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainAuditTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainFileListView;
import com.io7m.cardant_gui.ui.internal.CAGMainFileSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainLocationDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGMainLocationSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainLocationTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainStockSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainStockTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainTypePackageDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGMainTypePackageSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainTypePackageTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainView;
import com.io7m.cardant_gui.ui.internal.CAGStatusService;
import com.io7m.cardant_gui.ui.internal.CAGStringConstants;
import com.io7m.cardant_gui.ui.internal.CAGStrings;
import com.io7m.cardant_gui.ui.internal.CAGStringsType;
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

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The main application class responsible for starting up the "main" view.
 */

public final class CAGApplication extends Application
{
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
      CAGItemAttachmentAddDialogs.class,
      new CAGItemAttachmentAddDialogs(services)
    );
    services.register(
      CAGLocationAttachmentAddDialogs.class,
      new CAGLocationAttachmentAddDialogs(services)
    );
    services.register(
      CAGLocationReparentDialogs.class,
      new CAGLocationReparentDialogs(services)
    );

    final var status = new CAGStatusService();
    services.register(CAGStatusService.class, status);

    final var clientService = new CAGClientService(status, strings);
    services.register(CAGClientServiceType.class, clientService);

    final var controller = CAGController.create(clientService);
    services.register(CAGControllerType.class, controller);

    final var xml =
      CAGMainView.class.getResource(
        "/com/io7m/cardant_gui/ui/internal/main.fxml"
      );
    final var resources =
      strings.resources();
    final var loader =
      new FXMLLoader(xml, resources);

    final Map<Class<? extends CAGViewType>, Supplier<CAGViewType>> controllers =
      Map.ofEntries(
        Map.entry(
          CAGMainView.class,
          () -> new CAGMainView(services)
        ),
        Map.entry(
          CAGMainItemDetailsView.class,
          () -> new CAGMainItemDetailsView(services)
        ),
        Map.entry(
          CAGMainItemSearchView.class,
          () -> new CAGMainItemSearchView(services)
        ),
        Map.entry(
          CAGMainStockSearchView.class,
          () -> new CAGMainStockSearchView(services)
        ),
        Map.entry(
          CAGMainStockTableView.class,
          () -> new CAGMainStockTableView(services)
        ),
        Map.entry(
          CAGMainItemTableView.class,
          () -> new CAGMainItemTableView(services)
        ),
        Map.entry(
          CAGMainFileSearchView.class,
          () -> new CAGMainFileSearchView(services)
        ),
        Map.entry(
          CAGMainFileListView.class,
          () -> new CAGMainFileListView(services)
        ),
        Map.entry(
          CAGMainAuditSearchView.class,
          () -> new CAGMainAuditSearchView(services)
        ),
        Map.entry(
          CAGMainAuditTableView.class,
          () -> new CAGMainAuditTableView(services)
        ),
        Map.entry(
          CAGMainTypePackageDetailsView.class,
          () -> new CAGMainTypePackageDetailsView(services)
        ),
        Map.entry(
          CAGMainTypePackageSearchView.class,
          () -> new CAGMainTypePackageSearchView(services)
        ),
        Map.entry(
          CAGMainTypePackageTableView.class,
          () -> new CAGMainTypePackageTableView(services)
        ),
        Map.entry(
          CAGMainLocationDetailsView.class,
          () -> new CAGMainLocationDetailsView(services)
        ),
        Map.entry(
          CAGMainLocationSearchView.class,
          () -> new CAGMainLocationSearchView(services)
        ),
        Map.entry(
          CAGMainLocationTableView.class,
          () -> new CAGMainLocationTableView(services)
        )
      );

    loader.setControllerFactory(
      clazz -> {
        final var supplier = controllers.get(clazz);
        if (supplier == null) {
          throw new IllegalStateException(
            "Unrecognized controller class: %s".formatted(clazz)
          );
        }
        return supplier.get();
      }
    );

    final var pane = loader.<Pane>load();
    CAGCSS.setCSS(pane);
    stage.setScene(new Scene(pane));
    stage.setTitle(strings.format(CAGStringConstants.CARDANT_TITLE));
    stage.show();
  }
}
