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


package com.io7m.cardant_gui.ui.internal.database;

import com.io7m.darco.api.DDatabaseUnit;
import org.jooq.DSLContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.io7m.cardant_gui.ui.internal.database.Tables.RECENT_FILES;

/**
 * List recent files.
 */

public final class CAGRecentFileList
  extends CAGDatabaseQueryAbstract<DDatabaseUnit, List<Path>>
  implements CAGRecentFileListType
{
  CAGRecentFileList(
    final CAGDatabaseTransactionType t)
  {
    super(t);
  }

  /**
   * @return The query provider
   */

  public static CAGDatabaseQueryProviderType<
    DDatabaseUnit, List<Path>, CAGRecentFileListType>
  provider()
  {
    return CAGDatabaseQueryProvider.provide(
      CAGRecentFileListType.class,
      CAGRecentFileList::new
    );
  }

  @Override
  protected List<Path> onExecute(
    final CAGDatabaseTransactionType transaction,
    final DDatabaseUnit parameters)
  {
    final var context =
      transaction.get(DSLContext.class);

    final var records =
      context.select(RECENT_FILES.RF_NAME)
        .from(RECENT_FILES)
        .orderBy(RECENT_FILES.RF_TIME.desc())
        .fetch();

    final var results = new ArrayList<Path>();
    for (final var record : records) {
      results.add(Paths.get(record.get(RECENT_FILES.RF_NAME)));
    }
    return List.copyOf(results);
  }
}
