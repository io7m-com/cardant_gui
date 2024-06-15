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

import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main type package search view.
 */

public final class CAGMainTypePackageSearchView
  implements CAGViewType
{
  private final CAGControllerType controller;
  private final CAGStringsType strings;

  @FXML private ChoiceBox<CAGDescriptionMatchKind> descriptionMatch;
  @FXML private TextArea description;
  @FXML private Accordion accordion;
  @FXML private TitledPane basicParameters;

  /**
   * The main type package search view.
   *
   * @param services The service directory
   */

  public CAGMainTypePackageSearchView(
    final RPServiceDirectoryType services)
  {
    this.controller =
      services.requireService(CAGControllerType.class);
    this.strings =
      services.requireService(CAGStringsType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.accordion.setExpandedPane(this.basicParameters);

    this.descriptionMatch.setItems(
      FXCollections.observableArrayList(CAGDescriptionMatchKind.values()));
    this.descriptionMatch.setConverter(
      new CAGDescriptionMatchConverter(this.strings));
    this.descriptionMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onDescriptionMatchChanged(newValue);
      });
    this.descriptionMatch.setValue(CAGDescriptionMatchKind.ANY);
  }

  @FXML
  private void onSearchSelected()
  {
    this.controller.typePackageSearchBegin(
      new CATypePackageSearchParameters(
        this.descriptionMatch(),
        100L
      )
    );
  }

  @FXML
  private void onSearchClearSelected()
  {

  }

  private CAComparisonFuzzyType<String> descriptionMatch()
  {
    return switch (this.descriptionMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonFuzzyType.Anything<>();
      }
      case EQUAL_TO -> {
        yield new CAComparisonFuzzyType.IsEqualTo<>(
          this.description.getText().trim()
        );
      }
      case NOT_EQUAL_TO -> {
        yield new CAComparisonFuzzyType.IsNotEqualTo<>(
          this.description.getText().trim()
        );
      }
      case SIMILAR_TO -> {
        yield new CAComparisonFuzzyType.IsSimilarTo<>(
          this.description.getText().trim()
        );
      }
      case NOT_SIMILAR_TO -> {
        yield new CAComparisonFuzzyType.IsNotSimilarTo<>(
          this.description.getText().trim()
        );
      }
    };
  }

  private void onDescriptionMatchChanged(
    final CAGDescriptionMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.description.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO, SIMILAR_TO, NOT_SIMILAR_TO -> {
        this.description.setDisable(false);
      }
    }
  }
}
