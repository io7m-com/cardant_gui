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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant_gui.ui.internal.database.CAGDatabaseType;
import com.io7m.cardant_gui.ui.internal.database.CAGRecentFileAddType;
import com.io7m.darco.api.DDatabaseException;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.tika.Tika;

import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The file creation view.
 */

public final class CAGFileCreateView
  implements CAGViewType
{
  private static final Tika TIKA = new Tika();

  private final CAGFileChoosersType choosers;
  private final Stage stage;
  private final ExecutorService executor;
  private final SimpleObjectProperty<FileDetails> fileDetails;
  private final CAGDatabaseType database;
  private final CAGFileTransferControllerType transfers;

  @FXML private TextField file;
  @FXML private TextField description;
  @FXML private TextField mediaType;
  @FXML private TextField size;
  @FXML private TextField hash;
  @FXML private ProgressBar hashProgress;
  @FXML private Button upload;

  /**
   * The file creation view.
   *
   * @param services The service directory
   * @param inStage  The stage
   */

  public CAGFileCreateView(
    final RPServiceDirectoryType services,
    final Stage inStage)
  {
    this.choosers =
      services.requireService(CAGFileChoosersType.class);
    this.database =
      services.requireService(CAGDatabaseType.class);
    this.transfers =
      services.requireService(CAGFileTransferControllerType.class);

    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.executor =
      Executors.newVirtualThreadPerTaskExecutor();

    this.stage.setWidth(800.0);
    this.stage.setHeight(300.0);

    this.stage.setOnCloseRequest(event -> {
      event.consume();
      this.executor.close();
    });

    this.fileDetails =
      new SimpleObjectProperty<>();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.hashProgress.setVisible(false);

    this.fileDetails.addListener(
      (observable, oldValue, newValue) -> this.onFileSelected(newValue)
    );
    this.fileDetails.set(null);
  }

  private void onFileSelected(
    final FileDetails newValue)
  {
    if (newValue == null) {
      this.hashProgress.setVisible(false);
      this.hash.setText("");
      this.mediaType.setText("");
      this.size.setText("");
      this.upload.setDisable(true);
      return;
    }

    this.hashProgress.setVisible(false);
    this.hash.setText(newValue.hash);
    this.mediaType.setText(newValue.mediaType);
    this.size.setText(Long.toUnsignedString(newValue.size));
    this.upload.setDisable(false);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onUploadSelected()
  {
    final var details = this.fileDetails.get();

    this.transfers.fileUpload(
      new CAFileID(UUID.randomUUID()),
      details.file,
      details.mediaType,
      this.description.getText().trim()
    );

    this.stage.close();
  }

  @FXML
  private void onFileOpenSelected()
    throws Exception
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

    final var chosen = results.get(0);
    this.choosers.setMostRecentDirectory(chosen.getParent());
    this.addRecentFile(chosen);

    this.file.setText(chosen.toAbsolutePath().toString());
    this.hashProgress.setVisible(true);

    this.executor.execute(() -> {
      final FileDetails details;

      try {
        details = fileDetailsOf(chosen);
      } catch (final Exception e) {
        Platform.runLater(() -> this.fileDetails.set(null));
        return;
      }

      Platform.runLater(() -> this.fileDetails.set(details));
    });
  }

  private void addRecentFile(
    final Path newFile)
  {
    try (var t = this.database.openTransaction()) {
      t.query(CAGRecentFileAddType.class).execute(newFile);
      t.commit();
    } catch (final DDatabaseException e) {
      // Nothing can be done.
    }
  }

  private record FileDetails(
    Path file,
    String hash,
    String mediaType,
    long size)
  {

  }

  private static FileDetails fileDetailsOf(
    final Path file)
    throws Exception
  {
    final var mediaTypeDetected =
      TIKA.detect(file);
    final var size =
      Files.size(file);
    final String hashValue =
      hashOf(file);

    return new FileDetails(
      file,
      hashValue,
      mediaTypeDetected,
      size
    );
  }

  private static String hashOf(
    final Path path)
    throws Exception
  {
    final var digest =
      MessageDigest.getInstance("SHA-256");

    try (var stream = Files.newInputStream(path)) {
      try (var digestStream = new DigestInputStream(stream, digest)) {
        digestStream.transferTo(OutputStream.nullOutputStream());
      }
    }

    return HexFormat.of().formatHex(digest.digest());
  }
}
