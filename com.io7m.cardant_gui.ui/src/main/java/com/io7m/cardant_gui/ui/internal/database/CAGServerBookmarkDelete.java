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

import static com.io7m.cardant_gui.ui.internal.database.Tables.SERVER_BOOKMARKS;

/**
 * Delete bookmarks.
 */

public final class CAGServerBookmarkDelete
  extends CAGDatabaseQueryAbstract<String, DDatabaseUnit>
  implements CAGServerBookmarkDeleteType
{
  CAGServerBookmarkDelete(
    final CAGDatabaseTransactionType t)
  {
    super(t);
  }

  /**
   * @return The query provider
   */

  public static CAGDatabaseQueryProviderType<
    String, DDatabaseUnit, CAGServerBookmarkDeleteType>
  provider()
  {
    return CAGDatabaseQueryProvider.provide(
      CAGServerBookmarkDeleteType.class,
      CAGServerBookmarkDelete::new
    );
  }

  @Override
  protected DDatabaseUnit onExecute(
    final CAGDatabaseTransactionType transaction,
    final String name)
  {
    final var context =
      transaction.get(DSLContext.class);

    context.deleteFrom(SERVER_BOOKMARKS)
      .where(SERVER_BOOKMARKS.SB_NAME.eq(name))
      .execute();

    return DDatabaseUnit.UNIT;
  }
}
