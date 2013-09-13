package org.library.web;

import java.util.ArrayList;
import java.util.List;

import org.library.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

/**
 * A {@link ResourceAssembler} for an {@link Iterable} of {@link Book}s. This is used to convert the
 * {@link Book}s to {@link Resources}.
 * 
 * @author dylants
 * 
 */
@Component
public class BooksResourceAssembler
        implements
            ResourceAssembler<Iterable<Book>, Resources<Resource<Book>>> {

    private final BookResourceAssembler bookResourceAssembler;

    /**
     * Creates a {@link BooksResourceAssembler}
     * 
     * @param bookResourceAssembler
     *            The necessary {@link BookResourceAssembler}
     */
    @Autowired
    public BooksResourceAssembler(BookResourceAssembler bookResourceAssembler) {
        this.bookResourceAssembler = bookResourceAssembler;
    }

    @Override
    public Resources<Resource<Book>> toResource(Iterable<Book> books) {
        List<Resource<Book>> bookResources = new ArrayList<>();

        // loop over the books, and for each one, create a Resource, add it to
        // our List of Resource's
        for (Book book : books) {
            bookResources.add(this.bookResourceAssembler.toResource(book));
        }

        // create a Resources for all our Resource's
        Resources<Resource<Book>> resources = new Resources<>(bookResources);

        return resources;
    }

}