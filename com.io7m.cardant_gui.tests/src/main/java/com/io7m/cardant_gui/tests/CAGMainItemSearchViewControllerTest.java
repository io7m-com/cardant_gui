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

import com.io7m.cardant_gui.ui.internal.CAGClientService;
import com.io7m.cardant_gui.ui.internal.CAGController;
import com.io7m.cardant_gui.ui.internal.CAGControllerType;
import com.io7m.cardant_gui.ui.internal.CAGMainItemSearchView;
import com.io7m.cardant_gui.ui.internal.CAGStatusService;
import com.io7m.cardant_gui.ui.internal.CAGStrings;
import com.io7m.cardant_gui.ui.internal.CAGStringsType;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.xoanon.commander.api.XCCommanderType;
import com.io7m.xoanon.commander.api.XCRobotType;
import com.io7m.xoanon.extension.XoExtension;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Locale;
import java.util.Objects;

@ExtendWith(XoExtension.class)
public final class CAGMainItemSearchViewControllerTest
{
  private CAGStrings strings;
  private RPServiceDirectory services;
  private Parent pane;
  private CAGMainItemSearchView view;
  private CAGStatusService status;
  private CAGClientService client;
  private CAGControllerType controller;

  @BeforeEach
  public void setup(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    this.strings = new CAGStrings(Locale.getDefault());
    this.status = new CAGStatusService();
    this.client = new CAGClientService(this.status, this.strings);
    this.controller = CAGController.create(this.client);

    this.services = new RPServiceDirectory();
    this.services.register(CAGStringsType.class, this.strings);
    this.services.register(CAGControllerType.class, this.controller);

    final var loader =
      new FXMLLoader(
        CAGMainItemSearchView.class.getResource(
          "/com/io7m/cardant_gui/ui/internal/itemSearch.fxml"),
        this.strings.resources()
      );

    loader.setControllerFactory(
      clazz -> {
        if (Objects.equals(clazz, CAGMainItemSearchView.class)) {
          return new CAGMainItemSearchView(this.services);
        }
        throw new IllegalStateException(
          "Unrecognized controller class: %s".formatted(clazz)
        );
      }
    );

    this.pane = loader.load();
    this.view = loader.getController();
  }

  @Test
  public void testInit(
    final XCCommanderType commander,
    final XCRobotType robot)
    throws Exception
  {
    robot.slowMotionEnable();

    final var stage =
      commander.stageNewAndWait(s -> {
        s.setWidth(800.0);
        s.setHeight(600.0);
        s.setScene(new Scene(this.pane));
        s.show();
      });
  }
}
