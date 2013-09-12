package org.library.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.library.domain.Book;
import org.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
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

    @Autowired
    public BooksController(BookRepository bookRepository,
            BookResourceAssembler bookResourceAssembler) {
        this.bookRepository = bookRepository;
        this.bookResourceAssembler = bookResourceAssembler;
    }

    /**
     * Returns all the {@link Book}s stored in our {@link BookRepository}
     * 
     * @return All the {@link Book}s stored in our {@link BookRepository}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Book> findAllBooks() {
        logger.debug("GET request on all books");
        return this.bookRepository.findAll();
    }

    /**
     * Returns the single book that has the <code>bookId</code>
     * 
     * @param request
     *            The {@link HttpServletRequest}
     * @param response
     *            The {@link HttpServletResponse}
     * @param bookId
     *            The ID of the book to find
     * @return The {@link Book}
     * @throws IOException
     */
    @RequestMapping(value = "/{bookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource<Book>> findBook(HttpServletRequest request,
            HttpServletResponse response, @PathVariable("bookId") Long bookId,
            @PathVariable("bookId") Book book) throws IOException {
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
     * @param request
     *            The {@link HttpServletRequest}
     * @param response
     *            The {@link HttpServletResponse}
     * @param book
     *            The incoming {@link Book} to be stored
     * @return The saved {@link Book}
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Book addBook(HttpServletRequest request, HttpServletResponse response,
            @RequestBody @Valid Book book) {
        // Here we're guaranteed to have a valid Book, so let's save it
        logger.debug("creating book {}", book);
        return this.bookRepository.save(book);
    }

    @RequestMapping(value = "/{bookId}", method = RequestMethod.PUT)
    @ResponseBody
    public Book updateBook(HttpServletRequest request, HttpServletResponse response,
            @PathVariable Long bookId, @RequestBody @Valid Book book) throws IOException {
        logger.debug("PUT request for book with id {}, updating book to {}", bookId, book);
        // attempt to find the book by ID
        Book existingBook = this.bookRepository.findOne(bookId);

        // if we found it, update it
        if (existingBook != null) {
            logger.debug("book to update found with data {}", existingBook);
            // verify the book we're to save has the correct ID
            book.setId(bookId);
            return this.bookRepository.save(book);
        } else {
            // else send a response not found
            logger.debug("book not found, returning 404 status code");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with ID " + bookId
                    + " not found");
            return null;
        }
    }

    @RequestMapping(value = "/{bookId}", method = RequestMethod.DELETE)
    public void deleteBook(HttpServletRequest request, HttpServletResponse response,
            @PathVariable Long bookId) throws IOException {
        logger.debug("DELETE request for book with id {}", bookId);
        // attempt to find the book by ID
        Book book = this.bookRepository.findOne(bookId);

        // if we found it, delete it
        if (book != null) {
            logger.debug("book found, deleting {}", book);
            this.bookRepository.delete(book);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // else send a response not found
            logger.debug("book not found, returning 404 status code");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with ID " + bookId
                    + " not found");
        }
    }
}