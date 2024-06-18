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

import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

/**
 * Audit methods for the controller.
 */

public interface CAGAuditControllerType
{
  /**
   * Start searching for audit records.
   *
   * @param searchParameters The search parameters
   */

  void auditSearchBegin(
    CAAuditSearchParameters searchParameters);

  /**
   * @return The audit records for the current search query
   */

  ObservableList<CAAuditEvent> auditEventsView();

  /**
   * @return The audit records for the current search query
   */

  SortedList<CAAuditEvent> auditEventsViewSorted();

  /**
   * @return The page range for the current audit event search query
   */

  ObservableValue<CAGPageRange> auditEventsPages();

  /**
   * Go to the next page of audit records.
   */

  void auditSearchNext();

  /**
   * Go to the previous page of audit records.
   */

  void auditSearchPrevious();
}
