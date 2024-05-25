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


package com.io7m.cardant_gui.ui;

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.jade.api.ApplicationDirectoriesType;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Functions to start the GUI.
 */

public final class CAGUI
{
  private final CAGApplication app;

  private CAGUI(
    final CAGApplication inApp)
  {
    this.app = Objects.requireNonNull(inApp, "app");
  }

  /**
   * Start a new UI.
   *
   * @param configuration The configuration
   * @param preferences
   *
   * @return A new UI
   *
   * @throws Exception On startup failures
   */

  public static CAGUI start(
    final ApplicationDirectoriesType configuration,
    final CAPreferencesServiceType preferences)
    throws Exception
  {
    final var stage = new Stage();
    stage.setWidth(1200.0);
    stage.setHeight(800.0);

    stage.setMinWidth(800.0);
    stage.setMinHeight(600.0);

    final var app = new CAGApplication(configuration, preferences);
    app.start(stage);
    return new CAGUI(app);
  }
}
