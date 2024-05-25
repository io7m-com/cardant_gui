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

import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.jwheatsheaf.api.JWFileChooserFilterType;
import com.io7m.jwheatsheaf.api.JWFileChooserType;
import com.io7m.jwheatsheaf.api.JWFileChoosersType;
import com.io7m.jwheatsheaf.oxygen.JWOxygenIconSet;
import com.io7m.jwheatsheaf.ui.JWFileChoosers;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * The file chooser service.
 */

public final class CAGFileChoosers implements CAGFileChoosersType
{
  private static final JWOxygenIconSet OXYGEN_ICON_SET =
    new JWOxygenIconSet();

  private final JWFileChoosersType choosers;

  /**
   * The file chooser service.
   */

  public CAGFileChoosers()
  {
    this.choosers =
      JWFileChoosers.createWith(
        Executors.newVirtualThreadPerTaskExecutor(),
        Locale.getDefault()
      );
  }

  @Override
  public JWFileChooserType create(
    final JWFileChooserConfiguration configuration)
  {
    return this.choosers.create(
      JWFileChooserConfiguration.builder()
        .from(configuration)
        .setFileImageSet(OXYGEN_ICON_SET)
        .build()
    );
  }

  @Override
  public JWFileChooserFilterType filterForAllFiles()
  {
    return this.choosers.filterForAllFiles();
  }

  @Override
  public JWFileChooserFilterType filterForOnlyDirectories()
  {
    return this.choosers.filterForOnlyDirectories();
  }

  @Override
  public String description()
  {
    return "File chooser service.";
  }

  @Override
  public void close()
    throws IOException
  {
    this.choosers.close();
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGFileChoosers 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
