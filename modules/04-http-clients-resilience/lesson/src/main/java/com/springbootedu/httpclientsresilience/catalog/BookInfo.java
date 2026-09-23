package com.springbootedu.httpclientsresilience.catalog;

import java.util.List;

/**
 * What the remote catalog knows about a book. Jackson maps the JSON response onto this record.
 */
public record BookInfo(String isbn, String title, List<String> authors, int pageCount) {
}
