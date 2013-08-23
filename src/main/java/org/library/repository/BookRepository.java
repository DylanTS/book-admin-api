package org.library.repository;

import org.library.domain.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Provides a repository interface around the {@link Book} object, by utilizing Spring's
 * {@link PagingAndSortingRepository}
 * 
 * @author dylants
 * 
 */
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

}