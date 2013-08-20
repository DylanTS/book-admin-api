package org.example.repository;

import org.example.domain.Book;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Provides a repository interface around the {@link Book} object, by utilizing Spring's
 * {@link PagingAndSortingRepository}
 * 
 * @author DylanTS
 * 
 */
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {

}