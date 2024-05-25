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

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * The nodes used to construct a mutable tree of nodes to match metadata.
 */

public sealed interface CAGMetaMatchNodeType
{
  /**
   * The type of element matches.
   */

  enum Element implements CAGMetaMatchNodeType
  {
    /**
     * Anything
     */
    ANYTHING,
    /**
     * The logical OR of two expressions.
     */
    OR,
    /**
     * The logical AND of two expressions.
     */
    AND,
    /**
     * A match expression.
     */
    MATCH
  }

  /**
   * A string comparison.
   */

  sealed interface StringComparisonNodeType
    extends CAGMetaMatchNodeType
  {
    /**
     * @return The field to which this comparison is associated
     */

    String fieldName();

    /**
     * @return The comparison value
     */

    SimpleStringProperty value();

    /**
     * Anything.
     *
     * @param fieldName The field to which this comparison is associated
     * @param value     The comparison value
     */

    record StringAnything(
      String fieldName,
      SimpleStringProperty value)
      implements StringComparisonNodeType
    {

    }

    /**
     * Exactly equal to.
     *
     * @param fieldName The field to which this comparison is associated
     * @param value     The comparison value
     */

    record StringEqualTo(
      String fieldName,
      SimpleStringProperty value)
      implements StringComparisonNodeType
    {

    }

    /**
     * Not equal to.
     *
     * @param fieldName The field to which this comparison is associated
     * @param value     The comparison value
     */

    record StringNotEqualTo(
      String fieldName,
      SimpleStringProperty value)
      implements StringComparisonNodeType
    {

    }
  }

  /**
   * A value match expression.
   */

  sealed interface ValueType
    extends CAGMetaMatchNodeType
  {
    /**
     * Anything.
     */

    record ValueAnything()
      implements ValueType
    {

    }

    /**
     * Matches a value in the given inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record ValueIntegerWithinRange(
      SimpleLongProperty lower,
      SimpleLongProperty upper)
      implements ValueType
    {

    }

    /**
     * Matches a value in the given inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record ValueRealWithinRange(
      SimpleDoubleProperty lower,
      SimpleDoubleProperty upper)
      implements ValueType
    {

    }

    /**
     * Matches a value in the given inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record ValueTimeWithinRange(
      SimpleObjectProperty<OffsetDateTime> lower,
      SimpleObjectProperty<OffsetDateTime> upper)
      implements ValueType
    {

    }

    /**
     * Matches a value in the given inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record ValueMoneyWithinRange(
      SimpleObjectProperty<BigDecimal> lower,
      SimpleObjectProperty<BigDecimal> upper)
      implements ValueType
    {

    }

    /**
     * Matches a value with the given currency.
     *
     * @param unit The currency unit
     */

    record ValueMoneyWithCurrency(
      SimpleObjectProperty<CurrencyUnit> unit)
      implements ValueType
    {

    }

    /**
     * Matches a value with the given exact text.
     *
     * @param text The text
     */

    record ValueExactText(
      SimpleStringProperty text)
      implements ValueType
    {

    }

    /**
     * Matches a value that matches the given search query.
     *
     * @param text The text
     */

    record ValueSearchText(
      SimpleStringProperty text)
      implements ValueType
    {

    }
  }
}
