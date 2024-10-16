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

import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseType;
import com.io7m.cardant_gui.ui.internal.database.CAGServerBookmarkDeleteType;
import com.io7m.cardant_gui.ui.internal.database.CAGServerBookmarkListType;
import com.io7m.cardant_gui.ui.internal.database.CAGServerBookmarkPutType;
import com.io7m.darco.api.DDatabaseException;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOGIN_BOOKMARK_CREATEMAIN;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_LOGIN_BOOKMARK_CREATETITLE;
import static com.io7m.darco.api.DDatabaseUnit.UNIT;

/**
 * The main login controller.
 */

public final class CAGLoginView
  implements CAGViewType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGLoginView.class);

  private final Stage stage;
  private final RPServiceDirectoryType mainServices;
  private final CAGDatabaseType database;
  private final CAGStringsType strings;
  private final CAGClientServiceType clientService;

  @FXML private Button bookmarkCreate;
  @FXML private Button bookmarkDelete;
  @FXML private Button cancelButton;
  @FXML private Button loginButton;
  @FXML private TextField hostField;
  @FXML private Label hostFieldBad;
  @FXML private TextField portField;
  @FXML private Label portFieldBad;
  @FXML private TextField userField;
  @FXML private Label userFieldBad;
  @FXML private PasswordField passField;
  @FXML private Label passFieldBad;
  @FXML private CheckBox httpsBox;
  @FXML private GridPane grid;
  @FXML private ComboBox<CAGServerBookmark> bookmarks;
  @FXML private HBox bookmarksContainer;

  /**
   * The login dialog controller.
   *
   * @param inMainServices The service directory
   * @param inStage        The host stage
   */

  CAGLoginView(
    final RPServiceDirectoryType inMainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");
    this.database =
      this.mainServices.requireService(CAGDatabaseType.class);
    this.strings =
      this.mainServices.requireService(CAGStringsType.class);
    this.clientService =
      this.mainServices.requireService(CAGClientServiceType.class);
  }

  @FXML
  private void onHTTPSBoxChanged()
  {
    this.validate();
  }

  private String uriStringNow()
  {
    final var builder = new StringBuilder(128);
    if (this.httpsBox.isSelected()) {
      builder.append("https://");
    } else {
      builder.append("http://");
    }
    builder.append(this.hostField.getCharacters());

    final var portText = this.portField.getCharacters();
    if (!portText.isEmpty()) {
      builder.append(":");
      builder.append(portText);
    }

    builder.append("/");
    return builder.toString();
  }

  @FXML
  private void onLogin()
  {
    final var connect =
      this.validate().orElseThrow();

    this.clientService.login(
      connect.host(),
      connect.port(),
      connect.isHTTPs(),
      connect.username(),
      connect.password()
    );

    this.stage.close();
  }

  private void bookmarkDeleteNow(
    final String name)
  {
    try {
      LOG.debug("Delete bookmark {}", name);

      try (var t = this.database.openTransaction()) {
        t.query(CAGServerBookmarkDeleteType.class).execute(name);
        t.commit();
      }
    } catch (final Exception e) {
      LOG.error("Unable to save bookmarks: ", e);
    }
  }

  private void bookmarkSaveNow(
    final CAGServerBookmark newBookmark)
  {
    try {
      LOG.debug("Save bookmark {}", newBookmark.name());

      try (var t = this.database.openTransaction()) {
        t.query(CAGServerBookmarkPutType.class).execute(newBookmark);
        t.commit();
      }
    } catch (final Exception e) {
      LOG.error("Unable to save bookmarks: ", e);
    }
  }

  @FXML
  private Optional<CAGServerBookmark> validate()
  {
    boolean ok = true;

    int port = 0;
    try {
      final var portText = this.portField.getCharacters().toString();
      if (!portText.isEmpty()) {
        port = Integer.parseUnsignedInt(portText);
      }
      this.portFieldBad.setVisible(false);
    } catch (final NumberFormatException e) {
      this.portFieldBad.setVisible(true);
      ok = false;
    }

    final var hostBad = this.hostField.getCharacters().isEmpty();
    if (hostBad) {
      this.hostFieldBad.setVisible(true);
      ok = false;
    } else {
      this.hostFieldBad.setVisible(false);
    }

    final var userBad = this.userField.getCharacters().isEmpty();
    if (userBad) {
      this.userFieldBad.setVisible(true);
      ok = false;
    } else {
      this.userFieldBad.setVisible(false);
    }

    final var passBad = this.passField.getCharacters().isEmpty();
    if (passBad) {
      this.passFieldBad.setVisible(true);
      ok = false;
    } else {
      this.passFieldBad.setVisible(false);
    }

    try {
      new URI(this.uriStringNow());
    } catch (final URISyntaxException e) {
      ok = false;
    }

    if (ok) {
      this.bookmarkCreate.setDisable(false);
      this.loginButton.setDisable(false);

      return Optional.of(
        new CAGServerBookmark(
          "",
          this.hostField.getCharacters().toString(),
          port,
          this.httpsBox.isSelected(),
          Duration.ofSeconds(10L),
          Duration.ofSeconds(10L),
          this.userField.getCharacters().toString(),
          this.passField.getCharacters().toString()
        ));
    }

    this.bookmarkCreate.setDisable(true);
    this.loginButton.setDisable(true);
    return Optional.empty();
  }

  @FXML
  private void onRequestBookmarkDelete()
  {
    final var selected =
      this.bookmarks.getSelectionModel()
        .getSelectedItem();

    if (selected == null) {
      return;
    }

    this.bookmarkDeleteNow(selected.name());
    this.reloadBookmarks();
  }

  @FXML
  private void onRequestBookmarkCreate()
  {
    final var dialog = new TextInputDialog();
    dialog.setTitle(this.strings.format(CARDANT_LOGIN_BOOKMARK_CREATETITLE));
    dialog.setHeaderText(null);
    dialog.setContentText(this.strings.format(CARDANT_LOGIN_BOOKMARK_CREATEMAIN));

    final var nameOpt = dialog.showAndWait();
    nameOpt.ifPresent(name -> {
      this.validate().ifPresent(newBookmark -> {
        this.bookmarkSaveNow(new CAGServerBookmark(
          name,
          newBookmark.host(),
          newBookmark.port(),
          newBookmark.isHTTPs(),
          newBookmark.loginTimeout(),
          newBookmark.commandTimeout(),
          newBookmark.username(),
          newBookmark.password()
        ));
        this.reloadBookmarks();
      });
    });
  }

  @FXML
  private void onCancel()
  {
    this.stage.close();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.hostFieldBad.setVisible(false);
    this.userFieldBad.setVisible(false);
    this.portFieldBad.setVisible(false);
    this.passFieldBad.setVisible(false);
    this.loginButton.setDisable(true);

    this.portField.setText("51000");

    this.bookmarks.setConverter(new CAGBookmarkStringConverter());
    this.bookmarks.getSelectionModel()
      .selectedItemProperty()
      .addListener(
        (observable, oldValue, newValue) ->
          this.onSelectedBookmark(newValue)
      );

    this.reloadBookmarks();
    this.bookmarks.getSelectionModel()
      .selectFirst();

    Platform.runLater(() -> {
      this.hostField.requestFocus();
    });
  }

  private void reloadBookmarks()
  {
    final var items = this.bookmarks.getItems();
    items.clear();

    try (var t = this.database.openTransaction()) {
      items.addAll(t.query(CAGServerBookmarkListType.class).execute(UNIT));
    } catch (final DDatabaseException e) {
      LOG.error("Unable to load bookmarks: ", e);
    }
  }

  private void onSelectedBookmark(
    final CAGServerBookmark bookmark)
  {
    if (bookmark == null) {
      return;
    }

    this.hostField.setText(bookmark.host());
    this.portField.setText(Integer.toUnsignedString(bookmark.port()));
    this.httpsBox.setSelected(bookmark.isHTTPs());
    this.userField.setText(bookmark.username());
    this.passField.setText(bookmark.password());

    this.validate();
  }
}
