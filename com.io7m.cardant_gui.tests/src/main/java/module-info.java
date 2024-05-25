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


open module com.io7m.cardant_gui.tests
{
  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.platform.commons;
  requires org.junit.platform.engine;
  requires org.junit.platform.launcher;

  requires com.io7m.cardant_gui.ui;
  requires com.io7m.cardant.client.api;
  requires com.io7m.cardant.client.basic;
  requires com.io7m.cardant.client.preferences.api;
  requires com.io7m.cardant.client.preferences.vanilla;
  requires com.io7m.cardant.parsers;

  requires com.io7m.repetoir.core;

  requires com.io7m.xoanon.extension;
  requires com.io7m.xoanon.commander.api;

  requires javafx.controls;
  requires javafx.fxml;

  exports com.io7m.cardant_gui.tests;
}