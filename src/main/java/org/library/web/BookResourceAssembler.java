package org.library.web;

import org.library.domain.Book;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

/**
 * A {@link ResourceAssembler} for {@link Book}s
 * 
 * @author dylants
 * 
 */
@Component
public class BookResourceAssembler implements ResourceAssembler<Book, Resource<Book>> {

    @Override
    public Resource<Book> toResource(Book book) {
        Resource<Book> resource = new Resource<Book>(book);
        return resource;
    }
}
