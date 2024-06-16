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
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The main controller.
 */

public final class CAGController implements CAGControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAGController.class);

  private final CAGClientServiceType clientService;
  private final ObservableList<CAAuditEvent> auditEvents;
  private final ObservableList<CAFileType.CAFileWithoutData> files;
  private final ObservableList<CATypePackageSummary> typePackages;
  private final SimpleObjectProperty<CAGPageRange> auditEventPages;
  private final SimpleObjectProperty<CAGPageRange> filePages;
  private final SimpleObjectProperty<CAGPageRange> typePackagePages;
  private final SimpleStringProperty typePackageTextSelected;
  private final SortedList<CAAuditEvent> auditEventsSorted;
  private final SortedList<CATypePackageSummary> typePackagesSorted;
  private final SimpleObjectProperty<CATypePackageIdentifier> typePackageSelected;

  private CAGController(
    final CAGClientServiceType inClientService)
  {
    this.clientService =
      Objects.requireNonNull(inClientService, "clientService");

    this.filePages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.files =
      FXCollections.observableArrayList();

    this.auditEventPages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.auditEvents =
      FXCollections.observableArrayList();
    this.auditEventsSorted =
      new SortedList<>(this.auditEvents);

    this.typePackagePages =
      new SimpleObjectProperty<>(new CAGPageRange(0L, 0L));
    this.typePackages =
      FXCollections.observableArrayList();
    this.typePackagesSorted =
      new SortedList<>(this.typePackages);

    this.typePackageTextSelected =
      new SimpleStringProperty();
    this.typePackageSelected =
      new SimpleObjectProperty<>();
  }

  /**
   * Create a controller.
   *
   * @param clients The client service
   *
   * @return A controller
   */

  public static CAGControllerType create(
    final CAGClientServiceType clients)
  {
    final var controller = new CAGController(clients);
    clients.status().subscribe((oldStatus, newStatus) -> {
      controller.onClientStatusChanged();
    });
    return controller;
  }

  private void onClientStatusChanged()
  {
    this.auditEvents.clear();
    this.files.clear();
  }

  @Override
  public String description()
  {
    return "Main controller";
  }

  @Override
  public void typePackageGet(
    final CATypePackageIdentifier id)
  {
    final var future =
      this.clientService.execute(new CAICommandTypePackageGetText(id));

    future.thenAccept(response -> {
      final String formatted = formatXML(response.data());

      Platform.runLater(() -> {
        this.typePackageSelected.set(id);
        this.typePackageTextSelected.set(formatted);
      });
    });
  }

  private static String formatXML(
    final String data)
  {
    try {
      final Transformer transformer =
        TransformerFactory.newInstance()
          .newTransformer();

      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(
        "{http://xml.apache.org/xslt}indent-amount",
        "2");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

      final StreamResult result =
        new StreamResult(new StringWriter());
      final StreamSource source =
        new StreamSource(new ByteArrayInputStream(data.getBytes(UTF_8)));

      transformer.transform(source, result);
      return result.getWriter().toString();
    } catch (final TransformerException e) {
      final var sw = new StringWriter();
      final var pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      return sw.toString();
    }
  }

  @Override
  public ObservableValue<String> typePackageTextSelected()
  {
    return this.typePackageTextSelected;
  }

  @Override
  public void typePackageSelectNothing()
  {
    this.typePackageTextSelected.set(null);
  }

  @Override
  public void auditSearchBegin(
    final CAAuditSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandAuditSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data = response.results();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} audit events", newItemPage.size());
        this.auditEventPages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.auditEvents.setAll(newItemPage);
      });
    });
  }

  @Override
  public ObservableList<CAAuditEvent> auditEventsView()
  {
    return this.auditEvents;
  }

  @Override
  public SortedList<CAAuditEvent> auditEventsViewSorted()
  {
    return this.auditEventsSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> auditEventsPages()
  {
    return this.auditEventPages;
  }

  @Override
  public void typePackageSearchBegin(
    final CATypePackageSearchParameters searchParameters)
  {
    final var future =
      this.clientService.execute(
        new CAICommandTypePackageSearchBegin(searchParameters)
      );

    future.thenAccept(response -> {
      Platform.runLater(() -> {
        final var data = response.data();

        final var newItemPage =
          new ArrayList<>(data.items());

        LOG.debug("Received {} type packages", newItemPage.size());
        this.typePackagePages.set(
          new CAGPageRange(
            (long) data.pageIndex(),
            (long) data.pageCount()
          )
        );
        this.typePackages.setAll(newItemPage);
      });
    });
  }

  @Override
  public ObservableList<CATypePackageSummary> typePackagesView()
  {
    return this.typePackages;
  }

  @Override
  public SortedList<CATypePackageSummary> typePackagesViewSorted()
  {
    return this.typePackagesSorted;
  }

  @Override
  public ObservableValue<CAGPageRange> typePackagesPages()
  {
    return this.typePackagePages;
  }

  @Override
  public void typePackageInstall(
    final Path file)
  {
    try {
      this.clientService.execute(
        new CAICommandTypePackageInstall(Files.readString(file, UTF_8))
      );
    } catch (final IOException e) {
      LOG.error("typePackageInstall: ", e);
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAGController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }
}
