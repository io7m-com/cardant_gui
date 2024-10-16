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

import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.nio.file.Path;

/**
 * Type package methods for the controller.
 */

public interface CAGTypePackagesControllerType
{
  /**
   * Start searching for type packages.
   *
   * @param searchParameters The search parameters
   */

  void typePackageSearchBegin(
    CATypePackageSearchParameters searchParameters);

  /**
   * @return The type packages for the current search query
   */

  ObservableList<CATypePackageSummary> typePackagesView();

  /**
   * @return The type packages for the current search query
   */

  SortedList<CATypePackageSummary> typePackagesViewSorted();

  /**
   * @return The page range for the current type package search query
   */

  ObservableValue<CAGPageRange> typePackagesPages();

  /**
   * Install a type package from the given file.
   *
   * @param file The file
   */

  void typePackageInstall(Path file);

  /**
   * Fetch a type package.
   *
   * @param id The type package ID
   */

  void typePackageGet(CATypePackageIdentifier id);

  /**
   * @return The text of the currently selected type package
   */

  ObservableValue<String> typePackageTextSelected();

  /**
   * Clear the current type package selection.
   */

  void typePackageSelectNothing();
}
