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

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant_gui.ui.internal.CAGAttachmentAddDialogs;
import com.io7m.cardant_gui.ui.internal.CAGCSS;
import com.io7m.cardant_gui.ui.internal.CAGClientService;
import com.io7m.cardant_gui.ui.internal.CAGClientServiceType;
import com.io7m.cardant_gui.ui.internal.CAGController;
import com.io7m.cardant_gui.ui.internal.CAGControllerType;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosers;
import com.io7m.cardant_gui.ui.internal.CAGFileChoosersType;
import com.io7m.cardant_gui.ui.internal.CAGFileViewDialogs;
import com.io7m.cardant_gui.ui.internal.CAGMainFileListView;
import com.io7m.cardant_gui.ui.internal.CAGMainFileSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemDetailsView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMainItemTableView;
import com.io7m.cardant_gui.ui.internal.CAGMainView;
import com.io7m.cardant_gui.ui.internal.CAGStatusService;
import com.io7m.cardant_gui.ui.internal.CAGStringConstants;
import com.io7m.cardant_gui.ui.internal.CAGStrings;
import com.io7m.cardant_gui.ui.internal.CAGStringsType;
import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.repetoir.core.RPServiceDirectory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;

/**
 * The main application class responsible for starting up the "main" view.
 */

public final class CAGApplication extends Application
{
  private final ApplicationDirectoriesType directories;
  private final CAPreferencesServiceType preferences;

  /**
   * The main application class responsible for starting up the "main" view.
   *
   * @param inConfiguration The configuration
   * @param inPreferences   The preferences
   */

  public CAGApplication(
    final ApplicationDirectoriesType inConfiguration,
    final CAPreferencesServiceType inPreferences)
  {
    this.directories =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.preferences =
      Objects.requireNonNull(inPreferences, "preferences");
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

    services.register(CAGFileChoosersType.class, new CAGFileChoosers());
    services.register(CAGStringsType.class, strings);

    services.register(
      CAGFileViewDialogs.class,
      new CAGFileViewDialogs(services));

    services.register(
      CAGAttachmentAddDialogs.class,
      new CAGAttachmentAddDialogs(services)
    );

    services.register(CAPreferencesServiceType.class, this.preferences);

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

    loader.setControllerFactory(
      clazz -> {
        if (Objects.equals(clazz, CAGMainView.class)) {
          return new CAGMainView(services);
        }
        if (Objects.equals(clazz, CAGMainItemDetailsView.class)) {
          return new CAGMainItemDetailsView(services);
        }
        if (Objects.equals(clazz, CAGMainItemSearchView.class)) {
          return new CAGMainItemSearchView(services);
        }
        if (Objects.equals(clazz, CAGMainItemTableView.class)) {
          return new CAGMainItemTableView(services);
        }
        if (Objects.equals(clazz, CAGMainFileSearchView.class)) {
          return new CAGMainFileSearchView(services);
        }
        if (Objects.equals(clazz, CAGMainFileListView.class)) {
          return new CAGMainFileListView(services);
        }
        throw new IllegalStateException(
          "Unrecognized controller class: %s".formatted(clazz)
        );
      }
    );

    final var pane = loader.<Pane>load();
    CAGCSS.setCSS(pane);
    stage.setScene(new Scene(pane));
    stage.setTitle(strings.format(CAGStringConstants.CARDANT_TITLE));
    stage.show();
  }
}
