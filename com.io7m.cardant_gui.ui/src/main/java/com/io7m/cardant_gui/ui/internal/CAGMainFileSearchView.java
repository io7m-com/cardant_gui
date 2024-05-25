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

import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsNotEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsNotSimilarTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsSimilarTo;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_CANCEL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEARTITLE;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 * The main file search view.
 */

public final class CAGMainFileSearchView
  implements CAGViewType
{
  private final CAGStringsType strings;
  private final CAGControllerType controller;

  @FXML private ChoiceBox<CAGFileDescriptionMatchKind> fileDescriptionMatch;
  @FXML private TextArea fileDescription;
  @FXML private ChoiceBox<CAGMediaTypeMatchKind> fileMediaTypeMatch;
  @FXML private TextField fileMediaType;
  @FXML private Spinner<Long> fileSizeLower;
  @FXML private Spinner<Long> fileSizeUpper;

  /**
   * The main file search view.
   *
   * @param services The service directory
   */

  public CAGMainFileSearchView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
    this.controller =
      services.requireService(CAGControllerType.class);
  }

  private void clearParameters()
  {
    this.fileDescriptionMatch.getSelectionModel()
      .select(CAGFileDescriptionMatchKind.ANY);
    this.fileDescription.setText("");

    this.fileMediaTypeMatch.getSelectionModel()
      .select(CAGMediaTypeMatchKind.ANY);
    this.fileMediaType.setText("");

    this.fileSizeLower.getValueFactory()
      .setValue(Long.valueOf(0L));
    this.fileSizeUpper.getValueFactory()
      .setValue(Long.valueOf(Long.MAX_VALUE));
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.fileDescription.setDisable(true);

    this.fileSizeLower.setValueFactory(
      new CAGSpinnerLongFactory());
    this.fileSizeUpper.setValueFactory(
      new CAGSpinnerLongFactory());

    this.fileDescriptionMatch.setItems(
      FXCollections.observableArrayList(CAGFileDescriptionMatchKind.values()));
    this.fileDescriptionMatch.setConverter(
      new CAGFileDescriptionMatchConverter(this.strings));
    this.fileDescriptionMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onDescriptionMatchChanged(newValue);
      });

    this.fileMediaTypeMatch.setItems(
      FXCollections.observableArrayList(CAGMediaTypeMatchKind.values()));
    this.fileMediaTypeMatch.setConverter(
      new CAGMediaTypeMatchConverter(this.strings));
    this.fileMediaTypeMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onMediaTypeMatchChanged(newValue);
      });

    this.clearParameters();
  }

  private void onMediaTypeMatchChanged(
    final CAGMediaTypeMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.fileMediaType.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO, SIMILAR_TO, NOT_SIMILAR_TO -> {
        this.fileMediaType.setDisable(false);
      }
    }
  }

  private void onDescriptionMatchChanged(
    final CAGFileDescriptionMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.fileDescription.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO, SIMILAR_TO, NOT_SIMILAR_TO -> {
        this.fileDescription.setDisable(false);
      }
    }
  }

  @FXML
  private void onSearchSelected()
  {
    this.controller.fileSearchBegin(
      new CAFileSearchParameters(
        this.descriptionMatch(),
        this.mediaTypeMatch(),
        this.sizeRange(),
        new CAFileColumnOrdering(CAFileColumn.BY_ID, true),
        100L
      )
    );
  }

  @FXML
  private void onSearchClearSelected()
  {
    final var confirmMessage =
      this.strings.format(CARDANT_SEARCH_CONFIRMCLEAR);
    final var clearButtonMessage =
      this.strings.format(CARDANT_SEARCH_CLEAR);

    final var confirm =
      new ButtonType(clearButtonMessage, OK_DONE);
    final var cancel =
      new ButtonType(this.strings.format(CARDANT_CANCEL), CANCEL_CLOSE);

    final var dialog =
      new Alert(CONFIRMATION, confirmMessage);

    CAGCSS.setCSS(dialog.getDialogPane());

    dialog.setHeaderText(
      this.strings.format(CARDANT_SEARCH_CONFIRMCLEARTITLE));

    final var dialogButtons =
      dialog.getButtonTypes();

    dialogButtons.clear();
    dialogButtons.add(cancel);
    dialogButtons.add(confirm);

    final var r = dialog.showAndWait();
    if (r.isEmpty()) {
      return;
    }

    if (r.get().equals(confirm)) {
      this.clearParameters();
    }
  }

  private CASizeRange sizeRange()
  {
    return new CASizeRange(
      this.fileSizeLower.getValue().longValue(),
      this.fileSizeUpper.getValue().longValue()
    );
  }

  private CAComparisonFuzzyType<String> mediaTypeMatch()
  {
    return switch (this.fileMediaTypeMatch.getValue()) {
      case ANY -> {
        yield new Anything<>();
      }
      case EQUAL_TO -> {
        yield new IsEqualTo<>(this.fileMediaType.getText().trim());
      }
      case NOT_EQUAL_TO -> {
        yield new IsNotEqualTo<>(this.fileMediaType.getText().trim());
      }
      case SIMILAR_TO -> {
        yield new IsSimilarTo<>(this.fileMediaType.getText().trim());
      }
      case NOT_SIMILAR_TO -> {
        yield new IsNotSimilarTo<>(this.fileMediaType.getText().trim());
      }
    };
  }

  private CAComparisonFuzzyType<String> descriptionMatch()
  {
    return switch (this.fileDescriptionMatch.getValue()) {
      case ANY -> {
        yield new Anything<>();
      }
      case EQUAL_TO -> {
        yield new IsEqualTo<>(this.fileDescription.getText().trim());
      }
      case NOT_EQUAL_TO -> {
        yield new IsNotEqualTo<>(this.fileDescription.getText().trim());
      }
      case SIMILAR_TO -> {
        yield new IsSimilarTo<>(this.fileDescription.getText().trim());
      }
      case NOT_SIMILAR_TO -> {
        yield new IsNotSimilarTo<>(this.fileDescription.getText().trim());
      }
    };
  }
}
