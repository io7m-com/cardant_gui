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

import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType.And;
import com.io7m.cardant.model.CAMetadataElementMatchType.Or;
import com.io7m.cardant.model.CAMetadataElementMatchType.Specific;
import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.AnyValue;
import com.io7m.cardant.model.CAMetadataValueMatchType.IntegralMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType.WithCurrency;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.ExactTextValue;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.Search;
import com.io7m.cardant.model.CAMetadataValueMatchType.TimeMatchType.WithinRange;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsNotEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringAnything;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.StringComparisonNodeType.StringNotEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueAnything;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueExactText;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueIntegerWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueMoneyWithCurrency;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueMoneyWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueRealWithinRange;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueSearchText;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.ValueType.ValueTimeWithinRange;
import com.io7m.lanark.core.RDottedName;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element.AND;
import static com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element.ANYTHING;
import static com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element.MATCH;
import static com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType.Element.OR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_EQUALTO;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_EXACT_NOTEQUALTO;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_AND;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_CHANGETYPE;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_OR;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_SPECIFIC;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_ANY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_EXACTTEXT;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_INTEGRALWITHIN;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_MONEYCURRENCY;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_MONEYWITHIN;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_REALWITHIN;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_SEARCHTEXT;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_VALUE_TIMEWITHIN;
import static com.io7m.cardant_gui.ui.internal.CAGStringConstants.CARDANT_ITEMSEARCH_METADATA_WRAPIN;

/**
 * A class that manages a metadata match expression as a tree of nodes.
 */

public final class CAGMetaMatchTree
{
  private final TreeView<CAGMetaMatchNodeType> treeView;
  private final CompilationSequence compilationSequence;
  private final CAGStringsType strings;
  private final ContextMenu elementMenu;
  private final ContextMenu comparisonMenu;
  private final ContextMenu valueMenu;

  /**
   * A class that manages a metadata match expression as a tree of nodes.
   *
   * @param inStrings  The string resources
   * @param inTreeView The tree view
   */

  public CAGMetaMatchTree(
    final CAGStringsType inStrings,
    final TreeView<CAGMetaMatchNodeType> inTreeView)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.treeView =
      Objects.requireNonNull(inTreeView, "treeView");

    this.compilationSequence =
      new CompilationSequence();

    this.elementMenu =
      new ContextMenu(this.makeMetadataElementMatchMenu());
    this.comparisonMenu =
      new ContextMenu(this.makeMetadataComparisonMenu());
    this.valueMenu =
      new ContextMenu(this.makeMetadataValueMenu());

    this.treeView.setRoot(new TreeItem<>(ANYTHING));
    this.treeView.setContextMenu(null);
    this.treeView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onMetadataMatchSelectionChanged(newValue);
      });

    this.treeView.setCellFactory(
      new CAGMetaMatchCellFactory(this.strings, this.compilationSequence)
    );
  }

  private static CAMetadataValueMatchType compileValueMatch(
    final TreeItem<ValueType> item)
  {
    return switch (item.getValue()) {
      case final ValueAnything v -> {
        yield AnyValue.ANY_VALUE;
      }
      case final ValueExactText v -> {
        yield new ExactTextValue(v.text().getValue());
      }
      case final ValueIntegerWithinRange v -> {
        yield new IntegralMatchType.WithinRange(
          v.lower().get(),
          v.upper().get()
        );
      }
      case final ValueMoneyWithCurrency v -> {
        yield new WithCurrency(v.unit().get());
      }
      case final ValueMoneyWithinRange v -> {
        yield new MonetaryMatchType.WithinRange(
          v.lower().get(),
          v.upper().get()
        );
      }
      case final ValueRealWithinRange v -> {
        yield new RealMatchType.WithinRange(
          v.lower().get(),
          v.upper().get()
        );
      }
      case final ValueSearchText v -> {
        yield new Search(v.text().get());
      }
      case final ValueTimeWithinRange v -> {
        yield new WithinRange(
          v.lower().get(),
          v.upper().get()
        );
      }
    };
  }

  private static CAComparisonExactType<String> compileComparisonText(
    final TreeItem<StringComparisonNodeType> item)
  {
    return switch (item.getValue()) {
      case final StringAnything ignored -> {
        yield new Anything<>();
      }
      case final StringEqualTo exactEqualTo -> {
        yield new IsEqualTo<>(
          exactEqualTo.value().get()
        );
      }
      case final StringNotEqualTo exactNotEqualTo -> {
        yield new IsNotEqualTo<>(
          exactNotEqualTo.value().get()
        );
      }
    };
  }

  @SuppressWarnings("unchecked")
  private static <A, B> TreeItem<B> itemCast(
    final TreeItem<A> item)
  {
    final var r0 = (TreeItem<Object>) item;
    return (TreeItem<B>) r0;
  }

  private Menu[] makeMetadataElementMatchMenu()
  {
    final var changeMenu =
      new Menu(this.strings.format(CARDANT_ITEMSEARCH_METADATA_CHANGETYPE));
    final var changeItems =
      changeMenu.getItems();

    changeItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_OR,
        this::onMetadataChangeToOr
      )
    );
    changeItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_AND,
        this::onMetadataChangeToAnd
      )
    );
    changeItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_ANY,
        this::onMetadataChangeToAny
      )
    );
    changeItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_SPECIFIC,
        this::onMetadataChangeToSpecific
      )
    );

    final var wrapMenu =
      new Menu(this.strings.format(CARDANT_ITEMSEARCH_METADATA_WRAPIN));
    final var wrapItems =
      wrapMenu.getItems();

    wrapItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_OR,
        this::onMetadataWrapOr
      )
    );
    wrapItems.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_AND,
        this::onMetadataWrapAnd
      )
    );

    return new Menu[]{
      changeMenu,
      wrapMenu,
    };
  }

  private Menu makeMetadataComparisonMenu()
  {
    final var menu =
      new Menu(this.strings.format(CARDANT_ITEMSEARCH_METADATA_CHANGETYPE));
    final var items =
      menu.getItems();

    items.add(
      this.menuItem(
        CARDANT_EXACT_EQUALTO,
        this::onMetadataChangeComparisonToEqualTo
      )
    );
    items.add(
      this.menuItem(
        CARDANT_EXACT_NOTEQUALTO,
        this::onMetadataChangeComparisonToNotEqualTo
      )
    );
    items.add(
      this.menuItem(
        CARDANT_EXACT_ANY,
        this::onMetadataChangeComparisonToAny
      )
    );
    return menu;
  }

  private Menu makeMetadataValueMenu()
  {
    final var menu =
      new Menu(this.strings.format(CARDANT_ITEMSEARCH_METADATA_CHANGETYPE));
    final var items =
      menu.getItems();

    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_ANY,
        this::onMetadataChangeValueAny
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_EXACTTEXT,
        this::onMetadataChangeValueExactText
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_SEARCHTEXT,
        this::onMetadataChangeValueSearchText
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_INTEGRALWITHIN,
        this::onMetadataChangeValueIntegralWithin
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_REALWITHIN,
        this::onMetadataChangeValueRealWithin
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_TIMEWITHIN,
        this::onMetadataChangeValueTimeWithin
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_MONEYCURRENCY,
        this::onMetadataChangeValueMonetaryCurrency
      )
    );
    items.add(
      this.menuItem(
        CARDANT_ITEMSEARCH_METADATA_VALUE_MONEYWITHIN,
        this::onMetadataChangeValueMonetaryWithin
      )
    );
    return menu;
  }

  private MenuItem menuItem(
    final CAGStringConstantType text,
    final Runnable action)
  {
    final var item = new MenuItem(this.strings.format(text));
    item.setOnAction(event -> action.run());
    return item;
  }

  private void onMetadataMatchSelectionChanged(
    final TreeItem<CAGMetaMatchNodeType> item)
  {
    if (item == null) {
      this.treeView.setContextMenu(null);
      return;
    }

    switch (item.getValue()) {
      case final Element e -> {
        this.treeView.setContextMenu(this.elementMenu);
      }
      case final StringComparisonNodeType c -> {
        this.treeView.setContextMenu(this.comparisonMenu);
      }
      case final ValueType v -> {
        this.treeView.setContextMenu(this.valueMenu);
      }
    }
  }

  /**
   * @return A property exposing the current tree sequence number
   */

  public ReadOnlyProperty<BigInteger> sequence()
  {
    return this.compilationSequence.sequence;
  }

  private Void onMetadataDoUpdate(
    final Supplier<Void> update)
  {
    try {
      return update.get();
    } finally {
      this.compilationSequence.update();
    }
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeToOr()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final Element e -> {
          yield switch (e) {
            case ANYTHING, MATCH -> {
              root.setValue(OR);
              root.getChildren()
                .setAll(
                  List.of(
                    new TreeItem<>(ANYTHING),
                    new TreeItem<>(ANYTHING)
                  )
                );
              yield null;
            }
            case OR -> {
              yield null;
            }
            case AND -> {
              root.setValue(OR);
              yield null;
            }
          };
        }
        case final StringComparisonNodeType c -> {
          yield null;
        }
        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeToAnd()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final Element e -> {
          yield switch (e) {
            case ANYTHING, MATCH -> {
              root.setValue(AND);
              root.getChildren()
                .setAll(
                  List.of(
                    new TreeItem<>(ANYTHING),
                    new TreeItem<>(ANYTHING)
                  )
                );
              yield null;
            }
            case AND -> {
              yield null;
            }
            case OR -> {
              root.setValue(AND);
              yield null;
            }
          };
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeToAny()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final Element e -> {
          root.setValue(ANYTHING);
          root.getChildren().clear();
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeToSpecific()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final Element e -> {
          yield switch (e) {
            case ANYTHING, AND, OR -> {
              root.setValue(MATCH);

              final var children =
                root.getChildren();

              children.setAll(
                List.of(
                  new TreeItem<>(
                    new StringAnything(
                      "Package",
                      new SimpleStringProperty("cardant")
                    )
                  ),
                  new TreeItem<>(
                    new StringAnything(
                      "Type",
                      new SimpleStringProperty("")
                    )
                  ),
                  new TreeItem<>(
                    new StringAnything(
                      "Field",
                      new SimpleStringProperty("")
                    )
                  ),
                  new TreeItem<>(
                    new ValueAnything()
                  )
                )
              );
              yield null;
            }
            case MATCH -> {
              yield null;
            }
          };
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeComparisonToAny()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final StringComparisonNodeType c -> {
          root.setValue(new StringAnything(c.fieldName(), c.value()));
          yield null;
        }

        case final Element e -> {
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeComparisonToNotEqualTo()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final StringComparisonNodeType c -> {
          root.setValue(new StringNotEqualTo(c.fieldName(), c.value()));
          yield null;
        }

        case final Element e -> {
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeComparisonToEqualTo()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final StringComparisonNodeType c -> {
          root.setValue(new StringEqualTo(c.fieldName(), c.value()));
          yield null;
        }

        case final ValueType v -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueExactText()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(
            new ValueExactText(new SimpleStringProperty(""))
          );
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueSearchText()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueSearchText(new SimpleStringProperty("")));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueIntegralWithin()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueIntegerWithinRange(
            new SimpleLongProperty(0L),
            new SimpleLongProperty(Long.MAX_VALUE)
          ));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueRealWithin()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueRealWithinRange(
            new SimpleDoubleProperty(0.0),
            new SimpleDoubleProperty(1000.0)
          ));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueTimeWithin()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          final var timeUpper =
            OffsetDateTime.now()
              .withSecond(0)
              .withNano(0);

          final var timeLower =
            timeUpper.minusWeeks(1L);

          root.setValue(new ValueTimeWithinRange(
            new SimpleObjectProperty<>(timeLower),
            new SimpleObjectProperty<>(timeUpper)
          ));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueMonetaryWithin()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueMoneyWithinRange(
            new SimpleObjectProperty<>(BigDecimal.ZERO),
            new SimpleObjectProperty<>(BigDecimal.TEN)
          ));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueMonetaryCurrency()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueMoneyWithCurrency(
            new SimpleObjectProperty<>(CurrencyUnit.USD)
          ));
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataChangeValueAny()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          root.setValue(new ValueAnything());
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataWrapAnd()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          final var parent = root.getParent();
          if (parent == null) {
            final var c = new TreeItem<CAGMetaMatchNodeType>(AND);
            c.getChildren().add(root);
            c.getChildren().add(new TreeItem<>(ANYTHING));
            this.treeView.setRoot(c);
            yield null;
          }

          parent.getChildren().remove(root);
          final var c = new TreeItem<CAGMetaMatchNodeType>(AND);
          c.getChildren().add(root);
          c.getChildren().add(new TreeItem<>(ANYTHING));
          parent.getChildren().add(c);
          yield null;
        }
      };
    });
  }

  /**
   * Perform an operation on the selected element.
   *
   * @return null
   */

  public Void onMetadataWrapOr()
  {
    final var root =
      this.treeView.getSelectionModel()
        .getSelectedItem();

    if (root == null) {
      return null;
    }

    return this.onMetadataDoUpdate(() -> {
      return switch (root.getValue()) {
        case final ValueType v -> {
          yield null;
        }

        case final StringComparisonNodeType c -> {
          yield null;
        }

        case final Element e -> {
          final var parent = root.getParent();
          if (parent == null) {
            final var c = new TreeItem<CAGMetaMatchNodeType>(OR);
            c.getChildren().add(root);
            c.getChildren().add(new TreeItem<>(ANYTHING));
            this.treeView.setRoot(c);
            yield null;
          }

          parent.getChildren().remove(root);
          final var c = new TreeItem<CAGMetaMatchNodeType>(OR);
          c.getChildren().add(root);
          c.getChildren().add(new TreeItem<>(ANYTHING));
          parent.getChildren().add(c);
          yield null;
        }
      };
    });
  }

  /**
   * Clear the tree.
   */

  public void clear()
  {
    this.treeView.setRoot(new TreeItem<>(ANYTHING));
  }

  /**
   * Compile the tree.
   *
   * @return A compiled metadata match expression
   */

  public CAMetadataElementMatchType compile()
  {
    return this.compileElementMatch(itemCast(this.treeView.getRoot()));
  }

  private CAMetadataElementMatchType compileElementMatch(
    final TreeItem<Element> item)
  {
    return switch (item.getValue()) {
      case ANYTHING -> {
        yield CAMetadataElementMatchType.ANYTHING;
      }

      case OR -> {
        final var children =
          item.getChildren();

        yield new Or(
          this.compileElementMatch(children.get(0)),
          this.compileElementMatch(children.get(1))
        );
      }

      case AND -> {
        final var children =
          item.getChildren();

        yield new And(
          this.compileElementMatch(children.get(0)),
          this.compileElementMatch(children.get(1))
        );
      }

      case MATCH -> {
        final var children =
          item.getChildren();

        yield new Specific(
          compileComparisonText(itemCast(children.get(0)))
            .map(RDottedName::new),
          compileComparisonText(itemCast(children.get(1))),
          compileComparisonText(itemCast(children.get(2))),
          compileValueMatch(itemCast(children.get(3)))
        );
      }
    };
  }

  private static final class CompilationSequence
    implements CAGMetaMatchTreeSequenceType
  {
    private final SimpleObjectProperty<BigInteger> sequence;

    CompilationSequence()
    {
      this.sequence =
        new SimpleObjectProperty<>(BigInteger.ZERO);
    }

    @Override
    public void update()
    {
      this.sequence.set(this.sequence.get().add(BigInteger.ONE));
    }
  }
}
