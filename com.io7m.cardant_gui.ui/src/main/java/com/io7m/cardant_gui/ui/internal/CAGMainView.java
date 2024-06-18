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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStatusEvent.Kind.IDLE;

/**
 * The main view controller.
 */

public final class CAGMainView
  implements CAGViewType
{
  private final RPServiceDirectoryType services;
  private final CAGClientServiceType clients;
  private final CAGStatusService status;
  private final CAGStringsType strings;

  @FXML private TabPane mainTabs;
  @FXML private ProgressBar mainProgress;
  @FXML private Label mainStatusText;
  @FXML private Node mainError;
  @FXML private Tab tabItems;
  @FXML private Tab tabLocations;
  @FXML private Tab tabStock;
  @FXML private Tab tabFiles;
  @FXML private Tab tabTypePackages;
  @FXML private Tab tabAudit;

  /**
   * The main view controller.
   *
   * @param inServices The services
   */

  public CAGMainView(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.clients =
      this.services.requireService(CAGClientServiceType.class);
    this.status =
      this.services.requireService(CAGStatusService.class);
    this.strings =
      this.services.requireService(CAGStringsType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.mainTabs.setVisible(false);
    this.mainProgress.setVisible(false);
    this.mainError.setVisible(false);

    this.status.subscribe(this::onStatusEvent);
    this.clients.status()
      .subscribe((oldStatus, newStatus) -> {
        Platform.runLater(() -> {
          this.onClientStatusChanged(newStatus);
        });
      });

    this.status.publish(new CAGStatusEvent(
      IDLE, this.strings.format(CAGStringConstants.CARDANT_UI_BOOT)
    ));
  }

  private void onStatusEvent(
    final CAGStatusEvent event)
  {
    this.mainStatusText.setText(event.message());

    this.mainProgress.setVisible(
      switch (event.kind()) {
        case ERROR -> false;
        case IDLE -> false;
        case RUNNING -> true;
        case RUNNING_LONG -> true;
      }
    );

    this.mainError.setVisible(
      switch (event.kind()) {
        case ERROR -> true;
        case IDLE -> false;
        case RUNNING -> false;
        case RUNNING_LONG -> false;
      }
    );

    this.mainTabs.setDisable(
      switch (event.kind()) {
        case ERROR -> false;
        case IDLE -> false;
        case RUNNING -> false;
        case RUNNING_LONG -> true;
      }
    );
  }

  private void onClientStatusChanged(
    final CAGClientStatus newStatus)
  {
    this.mainTabs.setVisible(
      switch (newStatus) {
        case NOT_CONNECTED -> false;
        case CONNECTING -> false;
        case CONNECTED -> true;
      }
    );

    this.mainTabs.setDisable(
      switch (newStatus) {
        case NOT_CONNECTED -> true;
        case CONNECTING -> true;
        case CONNECTED -> false;
      }
    );
  }

  @FXML
  private void onOpenConnect()
    throws IOException
  {
    final var stage = new Stage();

    final var xml =
      CAGMainView.class.getResource(
        "/com/io7m/cardant_gui/ui/internal/login.fxml"
      );
    final var resources =
      this.strings.resources();
    final var loader =
      new FXMLLoader(xml, resources);

    loader.setControllerFactory(
      clazz -> {
        return new CAGLoginView(this.services, stage);
      }
    );

    final Pane pane = loader.load();
    CAGCSS.setCSS(pane);

    loader.getController();
    stage.setScene(new Scene(pane));
    stage.setTitle(this.strings.format(CAGStringConstants.CARDANT_LOGIN_TITLE));
    stage.show();
  }

  @FXML
  private void onExitSelected()
  {
    Platform.exit();
  }

  @FXML
  private void onAboutSelected()
    throws IOException
  {
    final var stage = new Stage();

    final var xml =
      CAGMainView.class.getResource(
        "/com/io7m/cardant_gui/ui/internal/about.fxml"
      );
    final var resources =
      this.strings.resources();
    final var loader =
      new FXMLLoader(xml, resources);

    loader.setControllerFactory(
      clazz -> {
        return new CAGAboutView();
      }
    );

    final Pane pane = loader.load();
    CAGCSS.setCSS(pane);

    loader.getController();

    stage.setScene(new Scene(pane));
    stage.setResizable(false);
    stage.setTitle("Cardant");
    stage.show();
  }
}
