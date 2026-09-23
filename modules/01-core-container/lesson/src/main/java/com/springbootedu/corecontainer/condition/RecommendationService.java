package com.springbootedu.corecontainer.condition;

import com.springbootedu.corecontainer.book.Book;
import java.util.List;

/**
 * Lesson 3.5 — two implementations exist; configuration decides which one becomes a bean.
 */
public interface RecommendationService {

    List<Book> recommend();
}
