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


package com.io7m.cardant_gui.tests;

import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType.And;
import com.io7m.cardant.model.CAMetadataElementMatchType.Or;
import com.io7m.cardant.model.CAMetadataElementMatchType.Specific;
import com.io7m.cardant.model.CAMetadataValueMatchType.AnyValue;
import com.io7m.cardant.model.CAMetadataValueMatchType.IntegralMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.ExactTextValue;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.Search;
import com.io7m.cardant.model.CAMetadataValueMatchType.TimeMatchType;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsNotEqualTo;
import com.io7m.cardant_gui.ui.internal.CAGMainItemSearchView;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchNodeType;
import com.io7m.cardant_gui.ui.internal.CAGMetaMatchTree;
import com.io7m.cardant_gui.ui.internal.CAGStrings;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.xoanon.commander.api.XCCommanderType;
import com.io7m.xoanon.extension.XoExtension;
import javafx.scene.Parent;
import javafx.scene.control.TreeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(XoExtension.class)
public final class CAGMetaMatchTreeTest
{
  private CAGStrings strings;
  private RPServiceDirectory services;
  private Parent pane;
  private CAGMainItemSearchView controller;
  private TreeView<CAGMetaMatchNodeType> treeView;
  private CAGMetaMatchTree metaTree;

  @BeforeEach
  public void setup(
    final XCCommanderType commander)
    throws IOException
  {
    this.strings =
      new CAGStrings(Locale.getDefault());
    this.treeView =
      new TreeView<>();
    this.metaTree =
      new CAGMetaMatchTree(this.strings, this.treeView);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp0()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeComparisonToAny();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp1()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeComparisonToEqualTo();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp2()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeComparisonToNotEqualTo();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp3()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAny();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp4()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(null);

    this.metaTree.onMetadataChangeToOr();
    this.metaTree.onMetadataChangeToAnd();
    this.metaTree.onMetadataChangeToAny();
    this.metaTree.onMetadataChangeToSpecific();
    this.metaTree.onMetadataChangeComparisonToAny();
    this.metaTree.onMetadataChangeComparisonToNotEqualTo();
    this.metaTree.onMetadataChangeComparisonToEqualTo();
    this.metaTree.onMetadataChangeValueExactText();
    this.metaTree.onMetadataChangeValueSearchText();
    this.metaTree.onMetadataChangeValueIntegralWithin();
    this.metaTree.onMetadataChangeValueRealWithin();
    this.metaTree.onMetadataChangeValueTimeWithin();
    this.metaTree.onMetadataChangeValueMonetaryWithin();
    this.metaTree.onMetadataChangeValueMonetaryCurrency();
    this.metaTree.onMetadataChangeValueAny();
    this.metaTree.onMetadataWrapAnd();
    this.metaTree.onMetadataWrapOr();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * A no-op case.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNoOp5()
    throws Exception
  {
    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    final var e0 =
      this.metaTree.compile();

    this.metaTree.onMetadataChangeToSpecific();

    final var e1 =
      this.metaTree.compile();

    assertEquals(e0, e1);
  }

  /**
   * Element matches can be changed to OR.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAnyToOr()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(Or.class, e1);
  }

  /**
   * Element matches can be changed to AND.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAnyToAnd()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(And.class, e1);
  }

  /**
   * Element matches can be changed to OR.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAndToOr()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(Or.class, e1);
  }

  /**
   * Element matches can be changed to And.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAndToAnd()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(And.class, e1);
  }

  /**
   * Element matches can be changed to Or.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOrToOr()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(Or.class, e1);
  }

  /**
   * Element matches can be changed to And.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOrToAnd()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    final var e1 =
      this.metaTree.compile();

    assertNotEquals(e0, e1);
    assertInstanceOf(And.class, e1);
  }

  /**
   * Element matches can be changed to Specific.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOrToSpecific()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    final var e1 =
      this.metaTree.compile();

    assertInstanceOf(Specific.class, e1);
  }

  /**
   * Element wrapping.
   *
   * @throws Exception On errors
   */

  @Test
  public void testWrapOr0()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataWrapOr();

    final var e1 =
      this.metaTree.compile();

    final var e = assertInstanceOf(Or.class, e1);
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e0());
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e1());
  }

  /**
   * Element wrapping.
   *
   * @throws Exception On errors
   */

  @Test
  public void testWrapOr1()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToAnd();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(1));

    this.metaTree.onMetadataWrapOr();

    final var e1 =
      this.metaTree.compile();

    final var e =
      assertInstanceOf(And.class, e1);
    final var b =
      assertInstanceOf(Or.class, e.e1());

    assertEquals(CAMetadataElementMatchType.ANYTHING, b.e0());
    assertEquals(CAMetadataElementMatchType.ANYTHING, b.e1());
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e0());
  }

  /**
   * Element wrapping.
   *
   * @throws Exception On errors
   */

  @Test
  public void testWrapAnd0()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataWrapAnd();

    final var e1 =
      this.metaTree.compile();

    final var e = assertInstanceOf(And.class, e1);
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e0());
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e1());
  }

  /**
   * Element wrapping.
   *
   * @throws Exception On errors
   */

  @Test
  public void testWrapAnd1()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToOr();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(1));

    this.metaTree.onMetadataWrapAnd();

    final var e1 =
      this.metaTree.compile();

    final var e =
      assertInstanceOf(Or.class, e1);
    final var b =
      assertInstanceOf(And.class, e.e1());

    assertEquals(CAMetadataElementMatchType.ANYTHING, b.e0());
    assertEquals(CAMetadataElementMatchType.ANYTHING, b.e1());
    assertEquals(CAMetadataElementMatchType.ANYTHING, e.e0());
  }

  /**
   * Comparison conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testComparisonEquals0()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(0));

    this.metaTree.onMetadataChangeComparisonToEqualTo();

    final var e1 =
      this.metaTree.compile();

    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(IsEqualTo.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
  }

  /**
   * Comparison conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testComparisonEquals1()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(0));

    this.metaTree.onMetadataChangeComparisonToNotEqualTo();

    final var e1 =
      this.metaTree.compile();

    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(IsNotEqualTo.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
  }

  /**
   * Comparison conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testComparisonEquals2()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(0));

    this.metaTree.onMetadataChangeComparisonToNotEqualTo();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(0));

    this.metaTree.onMetadataChangeComparisonToAny();

    final var e1 =
      this.metaTree.compile();

    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue0()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueExactText();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(ExactTextValue.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue1()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueSearchText();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(Search.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue2()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueMonetaryWithin();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(MonetaryMatchType.WithinRange.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue3()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueMonetaryCurrency();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(MonetaryMatchType.WithCurrency.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue4()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueRealWithin();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(RealMatchType.WithinRange.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue5()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueIntegralWithin();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(IntegralMatchType.WithinRange.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue6()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueTimeWithin();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(TimeMatchType.WithinRange.class, e.value());
  }

  /**
   * Value conversion.
   *
   * @throws Exception On errors
   */

  @Test
  public void testValue7()
    throws Exception
  {
    final var e0 = this.metaTree.compile();
    assertEquals(CAMetadataElementMatchType.ANYTHING, e0);

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot());

    this.metaTree.onMetadataChangeToSpecific();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueTimeWithin();

    this.treeView.getSelectionModel()
      .select(this.treeView.getRoot().getChildren().get(3));

    this.metaTree.onMetadataChangeValueAny();

    final var e1 = this.metaTree.compile();
    final var e = assertInstanceOf(Specific.class, e1);
    assertInstanceOf(Anything.class, e.packageName());
    assertInstanceOf(Anything.class, e.fieldName());
    assertInstanceOf(Anything.class, e.typeName());
    assertInstanceOf(AnyValue.class, e.value());
  }
}
