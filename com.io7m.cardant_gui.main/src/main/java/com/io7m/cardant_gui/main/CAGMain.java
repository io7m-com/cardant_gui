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


package com.io7m.cardant_gui.main;

import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import com.io7m.cardant_gui.ui.CAGUI;
import com.io7m.jade.api.ApplicationDirectories;
import com.io7m.jade.api.ApplicationDirectoryConfiguration;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main entry point.
 */

public final class CAGMain
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGMain.class);

  private CAGMain()
  {

  }

  /**
   * The main entry point.
   *
   * @param args The command-line arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    final var directoryConfiguration =
      ApplicationDirectoryConfiguration.builder()
        .setApplicationName("com.io7m.cardant")
        .setPortablePropertyName("com.io7m.cardant.portable")
        .setOverridePropertyName("com.io7m.cardant.override")
        .build();

    final var directories =
      ApplicationDirectories.get(directoryConfiguration);

    final var preferences =
      CAPreferencesService.openOrDefault(
        directories.configurationDirectory()
          .resolve("preferences.xml")
      );

    Platform.startup(() -> {
      try {
        CAGUI.start(directories, preferences);
      } catch (final Exception e) {
        LOG.error("startup failed: ", e);
        System.exit(1);
      }
    });
  }
}
