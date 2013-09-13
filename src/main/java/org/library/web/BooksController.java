package org.library.web;

import javax.validation.Valid;

import org.library.domain.Book;
import org.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Provides REST API endpoints to access {@link Book}s
 * 
 * @author dylants
 * 
 */
@Controller
@RequestMapping(value = "/books", produces = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.TEXT_XML_VALUE})
public class BooksController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BookRepository bookRepository;
    private final BookResourceAssembler bookResourceAssembler;
    private final BooksResourceAssembler booksResourceAssembler;

    @Autowired
    public BooksController(BookRepository bookRepository,
            BookResourceAssembler bookResourceAssembler,
            BooksResourceAssembler booksResourceAssembler) {
        this.bookRepository = bookRepository;
        this.bookResourceAssembler = bookResourceAssembler;
        this.booksResourceAssembler = booksResourceAssembler;
    }

    /**
     * Returns all the {@link Book}s as {@link Resources} stored in our {@link BookRepository}
     * 
     * @return All the {@link Book}s as {@link Resources} stored in our {@link BookRepository}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resources<Resource<Book>>> findAllBooks() {
        logger.debug("GET request on all books");
        Iterable<Book> books = this.bookRepository.findAll();
        Resources<Resource<Book>> resources = this.booksResourceAssembler.toResource(books);
        return new ResponseEntity<Resources<Resource<Book>>>(resources, HttpStatus.OK);
    }

    /**
     * Returns the single book that has the <code>bookId</code>
     * 
     * @param book
     *            The {@link Book} to retrieve
     * @return The {@link Book} {@link Resource}
     */
    @RequestMapping(value = "/{bookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource<Book>> findBook(@PathVariable("bookId") Book book) {
        if (book == null) {
            logger.debug("book not found, returning 404 status code");
            return new ResponseEntity<Resource<Book>>(HttpStatus.NOT_FOUND);
        }

        logger.debug("book found, returning {}", book);
        Resource<Book> resource = this.bookResourceAssembler.toResource(book);
        return new ResponseEntity<Resource<Book>>(resource, HttpStatus.OK);
    }

    /**
     * Creates a new {@link Book} in our {@link BookRepository} based on the incoming
     * <code>book</code>
     * 
     * @param book
     *            The incoming {@link Book} to be stored
     * @return The saved {@link Book} {@link Resource}
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Resource<Book>> addBook(@RequestBody @Valid Book book) {
        // Here we're guaranteed to have a valid Book, so let's save it
        logger.debug("creating book {}", book);
        book = this.bookRepository.save(book);

        logger.debug("returning created book {}", book);
        return new ResponseEntity<Resource<Book>>(this.bookResourceAssembler.toResource(book),
                HttpStatus.CREATED);
    }

    /**
     * Updates an existing {@link Book}
     * 
     * @param bookId
     *            The ID of the {@link Book} to update
     * @param book
     *            The incoming {@link Book} used to update
     * @return The updated {@link Book} {@link Resource}
     */
    @RequestMapping(value = "/{bookId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Resource<Book>> updateBook(@PathVariable Long bookId,
            @RequestBody @Valid Book book) {
        logger.debug("PUT request for book with id {}, updating book to {}", bookId, book);
        // attempt to find the book by ID
        Book existingBook = this.bookRepository.findOne(bookId);

        // if it's not found, return 404
        if (existingBook == null) {
            logger.debug("book not found, returning 404 status code");
            return new ResponseEntity<Resource<Book>>(HttpStatus.NOT_FOUND);
        }

        logger.debug("book to update found with data {}", existingBook);
        // make sure the book we're to save has the correct ID
        book.setId(bookId);
        book = this.bookRepository.save(book);

        logger.debug("returning updated book {}", book);
        return new ResponseEntity<Resource<Book>>(this.bookResourceAssembler.toResource(book),
                HttpStatus.OK);
    }

    /**
     * Deletes an existing {@link Book}
     * 
     * @param bookId
     *            The ID of the {@link Book} to delete
     * @return The {@link ResponseEntity} with the {@link HttpStatus} code with the result of the
     *         operation
     */
    @RequestMapping(value = "/{bookId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        logger.debug("DELETE request for book with id {}", bookId);
        // attempt to find the book by ID
        Book book = this.bookRepository.findOne(bookId);

        // if it's not found, return 404
        if (book == null) {
            logger.debug("book not found, returning 404 status code");
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        logger.debug("book found, deleting {}", book);
        this.bookRepository.delete(book);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}