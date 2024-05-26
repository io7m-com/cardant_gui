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

import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Downloading;
import com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Idle;
import com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Uploading;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_FILES_TRANSFER_DOWNLOADING;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_FILES_TRANSFER_IDLE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_FILES_TRANSFER_UPLOADING;
import static com.io7m.cardant_gui.ui.internal.CAGTransferStatusType.Idle.IDLE;

/**
 * The main file list view.
 */

public final class CAGMainFileListView
  implements CAGViewType
{
  private final CAGStringsType strings;
  private final CAGControllerType controller;
  private final CAGFileViewDialogs dialogs;
  private final CAGFileChoosersType choosers;

  @FXML private Label resultsLabel;
  @FXML private Button fileDownload;
  @FXML private Button fileAdd;
  @FXML private Button fileRemove;
  @FXML private Label transferLabel;
  @FXML private ProgressBar transferProgress;
  @FXML private Label transferSize;
  @FXML private Label transferRate;
  @FXML private ListView<CAFileWithoutData> files;

  /**
   * The main file list view.
   *
   * @param services The service directory
   */

  public CAGMainFileListView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
    this.controller =
      services.requireService(CAGControllerType.class);
    this.dialogs =
      services.requireService(CAGFileViewDialogs.class);
    this.choosers =
      services.requireService(CAGFileChoosersType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.resultsLabel.setText("");

    this.fileAdd.setDisable(false);
    this.fileDownload.setDisable(true);
    this.fileRemove.setDisable(true);

    this.files.setCellFactory(new CAGFileCellFactory(this.strings));
    this.files.setItems(this.controller.filesView());
    this.files.getSelectionModel()
      .setSelectionMode(SelectionMode.SINGLE);
    this.files.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<? super CAFileWithoutData>) this::onFileSelectionChanged);

    this.controller.filesView()
      .addListener(this::onFilesViewChanged);

    this.controller.transferStatus()
      .addListener((observable, oldValue, newValue) -> {
        this.onTransferStatusChanged(newValue);
      });

    this.onTransferStatusChanged(IDLE);
  }

  private void onFilesViewChanged(
    final Change<? extends CAFileWithoutData> c)
  {
    final var size = c.getList().size();
    if (size > 0) {
      final var range =
        this.controller.filePages().getValue();

      this.resultsLabel.setText(
        this.strings.format(
          CAGStringConstants.CARDANT_FILESEARCH_PAGEOF,
          Long.valueOf(range.pageIndex()),
          Long.valueOf(range.pageCount())
        )
      );
    } else {
      this.resultsLabel.setText("");
    }
  }

  private void onFileSelectionChanged(
    final Change<? extends CAFileWithoutData> c)
  {
    final var selected = c.getList();
    if (selected.isEmpty()) {
      this.fileAdd.setDisable(false);
      this.fileDownload.setDisable(true);
      this.fileRemove.setDisable(true);
      return;
    }

    this.fileDownload.setDisable(false);
    this.fileRemove.setDisable(false);
  }

  private void onTransferStatusChanged(
    final CAGTransferStatusType newValue)
  {
    switch (newValue) {
      case final Idle idle -> {
        this.transferLabel.setText(
          this.strings.format(CARDANT_FILES_TRANSFER_IDLE)
        );
        this.transferRate.setText("");
        this.transferSize.setText("");
        this.transferProgress.setProgress(0.0);
      }
      case final Uploading uploading -> {
        this.transferLabel.setText(
          this.strings.format(CARDANT_FILES_TRANSFER_UPLOADING)
        );
        this.transferRate.setText(formatRate(uploading.statistics()));
        this.transferSize.setText(formatSize(uploading.statistics()));
        this.transferProgress.setProgress(
          uploading.statistics().percentNormalized()
        );
      }
      case final Downloading downloading -> {
        this.transferLabel.setText(
          this.strings.format(CARDANT_FILES_TRANSFER_DOWNLOADING)
        );
        this.transferRate.setText(formatRate(downloading.statistics()));
        this.transferSize.setText(formatSize(downloading.statistics()));
        this.transferProgress.setProgress(
          downloading.statistics().percentNormalized()
        );
      }
    }
  }

  private static String formatSize(
    final CAClientTransferStatistics statistics)
  {
    final var octetsExpected =
      (double) statistics.sizeExpected();
    final var octetsTransferred =
      (double) statistics.sizeTransferred();

    final var megaExpected =
      octetsExpected / 1_000_000.0;
    final var megaTransferred =
      octetsTransferred / 1_000_000.0;

    return String.format(
      "%.02f MB / %.02f MB",
      Double.valueOf(megaTransferred),
      Double.valueOf(megaExpected)
    );
  }

  private static String formatRate(
    final CAClientTransferStatistics statistics)
  {
    final var octetsRate =
      (double) statistics.octetsPerSecond();
    final var megaRate =
      octetsRate / 1_000_000.0;

    return String.format("%.02f MB/s", Double.valueOf(megaRate));
  }

  @FXML
  private void onFileAddSelected()
    throws IOException
  {
    this.dialogs.openDialogAndWait(null);
  }

  @FXML
  private void onFileRemoveSelected()
  {

  }

  @FXML
  private void onFileDownloadSelected()
    throws Exception
  {
    final var configuration =
      JWFileChooserConfiguration.builder()
        .setAction(JWFileChooserAction.CREATE)
        .setConfirmFileSelection(true)
        .build();

    final var chooser =
      this.choosers.create(configuration);
    final var results =
      chooser.showAndWait();

    if (results.isEmpty()) {
      return;
    }

    final var file =
      results.get(0);
    final var fileTmp =
      file.resolveSibling(file.getFileName() + ".tmp");
    final var fileValue =
      this.files.getSelectionModel()
        .getSelectedItem();

    this.controller.fileDownload(
      fileValue.id(),
      file,
      fileTmp,
      fileValue.size(),
      fileValue.hashAlgorithm(),
      fileValue.hashValue()
    );
  }

  @FXML
  private void onPageNextSelected()
  {

  }

  @FXML
  private void onPagePreviousSelected()
  {

  }
}
