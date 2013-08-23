package org.library.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.library.domain.Book;
import org.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Returns all the {@link Book}s stored in our {@link BookRepository}
     * 
     * @return All the {@link Book}s stored in our {@link BookRepository}
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Iterable<Book> finaAllBooks() {
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
    public Book findBook(HttpServletRequest request, HttpServletResponse response,
            @PathVariable Long bookId) throws IOException {
        // attempt to find the one book by ID
        Book book = this.bookRepository.findOne(bookId);

        // if we found it, return it
        if (book != null) {
            return book;
        } else {
            // else send a response not found
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book with ID " + bookId
                    + " not found");
            return null;
        }
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
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Book addBook(HttpServletRequest request, HttpServletResponse response,
            @RequestBody @Valid Book book) throws IOException {
        // Here we're guaranteed to have a valid Book, so let's save it
        return this.bookRepository.save(book);
    }

}