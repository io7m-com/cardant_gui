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

import com.io7m.cardant_gui.ui.internal.CAGServerBookmark;
import com.io7m.darco.api.DDatabaseUnit;
import org.jooq.DSLContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.io7m.cardant_gui.ui.internal.database.Tables.SERVER_BOOKMARKS;

/**
 * List bookmarks.
 */

public final class CAGServerBookmarkList
  extends CAGDatabaseQueryAbstract<DDatabaseUnit, List<CAGServerBookmark>>
  implements CAGServerBookmarkListType
{
  CAGServerBookmarkList(
    final CAGDatabaseTransactionType t)
  {
    super(t);
  }

  /**
   * @return The query provider
   */

  public static CAGDatabaseQueryProviderType<
    DDatabaseUnit, List<CAGServerBookmark>, CAGServerBookmarkListType>
  provider()
  {
    return CAGDatabaseQueryProvider.provide(
      CAGServerBookmarkListType.class,
      CAGServerBookmarkList::new
    );
  }

  @Override
  protected List<CAGServerBookmark> onExecute(
    final CAGDatabaseTransactionType transaction,
    final DDatabaseUnit parameters)
  {
    final var context =
      transaction.get(DSLContext.class);

    final var records =
      context.select(
          SERVER_BOOKMARKS.SB_NAME,
          SERVER_BOOKMARKS.SB_HOST,
          SERVER_BOOKMARKS.SB_PORT,
          SERVER_BOOKMARKS.SB_HTTPS,
          SERVER_BOOKMARKS.SB_USERNAME,
          SERVER_BOOKMARKS.SB_PASSWORD,
          SERVER_BOOKMARKS.SB_TIMEOUT_LOGIN_MS,
          SERVER_BOOKMARKS.SB_TIMEOUT_COMMAND_MS
        ).from(SERVER_BOOKMARKS)
        .orderBy(SERVER_BOOKMARKS.SB_NAME)
        .fetch();

    final var results = new ArrayList<CAGServerBookmark>();
    for (final var record : records) {
      results.add(
        new CAGServerBookmark(
          record.get(SERVER_BOOKMARKS.SB_NAME),
          record.get(SERVER_BOOKMARKS.SB_HOST),
          record.get(SERVER_BOOKMARKS.SB_PORT).intValue(),
          record.get(SERVER_BOOKMARKS.SB_HTTPS) != 0L,
          Duration.ofMillis(
            record.get(SERVER_BOOKMARKS.SB_TIMEOUT_LOGIN_MS).longValue()
          ),
          Duration.ofMillis(
            record.get(SERVER_BOOKMARKS.SB_TIMEOUT_COMMAND_MS).longValue()
          ),
          record.get(SERVER_BOOKMARKS.SB_USERNAME),
          record.get(SERVER_BOOKMARKS.SB_PASSWORD)
        )
      );
    }
    return List.copyOf(results);
  }
}
