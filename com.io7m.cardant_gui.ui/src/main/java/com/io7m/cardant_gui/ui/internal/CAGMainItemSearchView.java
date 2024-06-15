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

import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.parsers.CAMetadataMatchExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.prettyprint.JSXPrettyPrinterCodeStyle;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.seltzer.api.SStructuredErrorType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_CANCEL;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEAR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_SEARCH_CONFIRMCLEARTITLE;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

/**
 * The main item search view.
 */

public final class CAGMainItemSearchView
  implements CAGViewType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMainItemSearchView.class);

  private final CAGStringsType strings;
  private final CAMetadataMatchExpressions expressions;
  private final CAGControllerType controller;

  @FXML private ChoiceBox<CAGItemNameMatchKind> itemNameMatch;
  @FXML private ChoiceBox<CAGItemTypeMatchKind> itemTypeMatch;
  @FXML private Button itemTypeAdd;
  @FXML private Button itemTypeRemove;
  @FXML private TextField itemName;
  @FXML private TreeView<CAGMetaMatchNodeType> itemMetadataMatch;
  @FXML private TextArea itemMetadataCompiled;
  @FXML private ListView<CATypeRecordIdentifier> itemTypes;
  @FXML private Accordion accordion;
  @FXML private TitledPane basicParameters;

  private CAGMetaMatchTree metaTree;

  /**
   * The main item search view.
   *
   * @param services The service directory
   */

  public CAGMainItemSearchView(
    final RPServiceDirectoryType services)
  {
    this.strings =
      services.requireService(CAGStringsType.class);
    this.expressions =
      new CAMetadataMatchExpressions(CAStrings.create(Locale.getDefault()));
    this.controller =
      services.requireService(CAGControllerType.class);
  }

  private void clearParameters()
  {
    this.itemNameMatch.getSelectionModel()
      .select(CAGItemNameMatchKind.ANY);
    this.itemName.setText("");

    this.itemTypeMatch.getSelectionModel()
      .select(CAGItemTypeMatchKind.ANY);
    this.itemTypes.getItems()
      .clear();

    this.metaTree.clear();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.accordion.setExpandedPane(this.basicParameters);

    this.itemTypeRemove.setDisable(true);
    this.itemTypeAdd.setDisable(true);
    this.itemName.setDisable(true);

    this.itemTypeMatch.setItems(
      FXCollections.observableArrayList(CAGItemTypeMatchKind.values()));
    this.itemTypeMatch.setConverter(
      new CAGItemTypeMatchConverter(this.strings));
    this.itemTypeMatch.getSelectionModel()
      .select(CAGItemTypeMatchKind.ANY);
    this.itemTypeMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onTypeMatchChanged(newValue);
      });

    this.itemNameMatch.setItems(
      FXCollections.observableArrayList(CAGItemNameMatchKind.values()));
    this.itemNameMatch.setConverter(
      new CAGItemNameMatchConverter(this.strings));
    this.itemNameMatch.getSelectionModel()
      .select(CAGItemNameMatchKind.ANY);
    this.itemNameMatch.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onNameMatchChanged(newValue);
      });

    this.metaTree =
      new CAGMetaMatchTree(this.strings, this.itemMetadataMatch);

    this.metaTree.sequence().addListener(
      (observable, oldValue, newValue) -> {
        this.recompileMetadataMatch();
      });
  }

  private void onTypeMatchChanged(
    final CAGItemTypeMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.itemTypeRemove.setDisable(true);
        this.itemTypeAdd.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO, SUPERSET_OF, SUBSET_OF, OVERLAPPING -> {
        this.itemTypeRemove.setDisable(false);
        this.itemTypeAdd.setDisable(false);
      }
    }
  }

  private void onNameMatchChanged(
    final CAGItemNameMatchKind k)
  {
    switch (k) {
      case ANY -> {
        this.itemName.setDisable(true);
      }
      case EQUAL_TO, NOT_EQUAL_TO, SIMILAR_TO, NOT_SIMILAR_TO -> {
        this.itemName.setDisable(false);
      }
    }
  }

  private void recompileMetadataMatch()
  {
    try {
      final var expression =
        this.metaTree.compile();
      final var sexpr =
        this.expressions.metadataMatchSerialize(expression);

      try (var writer = new StringWriter()) {
        final var pretty =
          JSXPrettyPrinterCodeStyle.newPrinterWithWidthIndent(
            writer,
            60,
            2
          );

        pretty.print(sexpr);
        writer.flush();
        this.itemMetadataCompiled.setText(writer.toString());
      }
    } catch (final Exception e) {
      final var text = new StringBuilder();
      text.append(e.getMessage());
      text.append("\n");

      final ArrayList<Map.Entry<String, String>> entries;
      if (e instanceof final SStructuredErrorType<?> s) {
        entries = new ArrayList<>(s.attributes().entrySet());
      } else {
        entries = new ArrayList<>();
      }
      entries.sort(Map.Entry.comparingByKey());

      for (final var entry : entries) {
        text.append("  ");
        text.append(entry.getKey());
        text.append(": ");
        text.append(entry.getValue());
        text.append("\n");
      }

      this.itemMetadataCompiled.setText(text.toString());
      LOG.error("Compile expression: {}: ", text, e);
    }
  }

  @FXML
  private void onItemTypeAdd()
  {

  }

  @FXML
  private void onItemTypeRemove()
  {

  }

  @FXML
  private void onSearchSelected()
  {
    this.controller.itemSearchBegin(
      new CAItemSearchParameters(
        this.nameMatch(),
        new CAComparisonFuzzyType.Anything<>(),
        this.typeMatch(),
        this.metadataMatch(),
        new CAItemColumnOrdering(CAItemColumn.BY_NAME, true),
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

  private CAMetadataElementMatchType metadataMatch()
  {
    return this.metaTree.compile();
  }

  private CAComparisonSetType<CATypeRecordIdentifier> typeMatch()
  {
    return switch (this.itemTypeMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonSetType.Anything<>();
      }
      case EQUAL_TO -> {
        throw new IllegalStateException();
      }
      case NOT_EQUAL_TO -> {
        throw new IllegalStateException();
      }
      case SUPERSET_OF -> {
        throw new IllegalStateException();
      }
      case SUBSET_OF -> {
        throw new IllegalStateException();
      }
      case OVERLAPPING -> {
        throw new IllegalStateException();
      }
    };
  }

  private CAComparisonFuzzyType<String> nameMatch()
  {
    return switch (this.itemNameMatch.getValue()) {
      case ANY -> {
        yield new CAComparisonFuzzyType.Anything<>();
      }
      case EQUAL_TO -> {
        yield new CAComparisonFuzzyType.IsEqualTo<>(
          this.itemName.getText().trim()
        );
      }
      case NOT_EQUAL_TO -> {
        yield new CAComparisonFuzzyType.IsNotEqualTo<>(
          this.itemName.getText().trim()
        );
      }
      case SIMILAR_TO -> {
        yield new CAComparisonFuzzyType.IsSimilarTo<>(
          this.itemName.getText().trim()
        );
      }
      case NOT_SIMILAR_TO -> {
        yield new CAComparisonFuzzyType.IsNotSimilarTo<>(
          this.itemName.getText().trim()
        );
      }
    };
  }
}
