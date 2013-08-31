package org.library.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.library.WebApplicationInitializer;
import org.library.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.ServletContextConfiguration.class,
        WebApplicationInitializer.RootContextConfiguration.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class BooksControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private BooksController bookController;
    private MockMvc mockMvc;

    @BeforeClass
    public static void setupBeforeClass() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setupBefore() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testGET_AllBooks() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/books"));

        // verify the response is OK
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content to an array of books
        @SuppressWarnings("unchecked")
        List<Book> books = objectMapper.readValue(content, List.class);

        // verify there are books
        Assert.assertNotNull("books must exist", books);
        Assert.assertTrue("there must be at least one book", books.size() > 0);
    }

    @Test
    public void testGET_SingleBook() throws Exception {
        // perform the GET request
        ResultActions actions = this.mockMvc.perform(get("/books/1"));

        // verify the response is OK
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book book = objectMapper.readValue(content, Book.class);

        // verify the book details
        Assert.assertEquals("id must match", (long) 1L, (long) book.getId());
        Assert.assertEquals("title must match", "The Elegant Universe: Superstrings, Hidden "
                + "Dimensions, and the Quest for the Ultimate Theory", book.getTitle());
        Assert.assertEquals("author must match", "Brian Greene", book.getAuthor());
    }

    @Test
    public void testGET_BookThatDoesNotExist() throws Exception {
        String bookId = "99999999";
        // perform the GET request
        ResultActions actions = this.mockMvc.perform(get("/books/" + bookId));

        // verify the response is not found
        actions.andExpect(status().isNotFound());

        // verify status message
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be nulL", result);
        String errorMessage = result.getResponse().getErrorMessage();
        Assert.assertTrue("errorMessage must exist", StringUtils.isNotBlank(errorMessage));
        System.out.println(errorMessage);

        Assert.assertEquals("correct error message must exist", "Book with ID " + bookId
                + " not found", errorMessage);
    }

    @Test
    public void testPOST_CreateABook() throws Exception {
        String title = "Jamberry";
        String author = "Bruce Degen";
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        // perform the POST request
        ResultActions actions = this.mockMvc.perform(post("/books").contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(book)));

        // verify the response is OK
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = objectMapper.readValue(content, Book.class);

        // verify the book details
        Assert.assertTrue("id must exist", createdBook.getId() != null);
        Assert.assertEquals("title must match", title, createdBook.getTitle());
        Assert.assertEquals("author must match", author, createdBook.getAuthor());
    }

    @Test
    public void testPUT_CreateAndUpdateBook() throws Exception {
        String title = "The Cat in the Hat";
        String author = "Dr. Suess";
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        // perform the POST request
        ResultActions actions = this.mockMvc.perform(post("/books").contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(book)));

        // verify the response is OK
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = objectMapper.readValue(content, Book.class);

        // perform the PUT to update the book
        String updatedTitle = "Green Eggs and Ham";
        createdBook.setTitle(updatedTitle);
        actions = this.mockMvc.perform(put("/books/" + createdBook.getId()).contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(createdBook)));

        // verify the response is OK
        actions.andExpect(status().isOk());
        result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book updatedBook = objectMapper.readValue(content, Book.class);

        // verify the book details
        Assert.assertEquals("id must match", createdBook.getId(), updatedBook.getId());
        Assert.assertEquals("title must match", updatedTitle, updatedBook.getTitle());
        Assert.assertEquals("author must match", createdBook.getAuthor(), updatedBook.getAuthor());
    }

    @Test
    public void testPUT_UpdateBookThatDoesNotExist() throws Exception {
        String title = "The Big Hungry Bear";
        String author = "Don and Audrey Wood";
        Book book = new Book();
        book.setId(99L);
        book.setTitle(title);
        book.setAuthor(author);

        // perform the PUT request
        ResultActions actions = this.mockMvc.perform(put("/books/" + book.getId()).contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(book)));

        // verify the response is not found
        actions.andExpect(status().is(404));
    }

    @Test
    public void testDELETE_CreateAndDeleteBook() throws Exception {
        String title = "Hug";
        String author = "Jez Alborough";
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);

        // perform the POST request
        ResultActions actions = this.mockMvc.perform(post("/books").contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(book)));

        // verify the response is OK
        actions.andExpect(status().isOk());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = objectMapper.readValue(content, Book.class);

        // perform the DELETE to delete the book
        actions = this.mockMvc.perform(delete("/books/" + createdBook.getId()).contentType(
                MediaType.APPLICATION_JSON));

        // verify the response is OK
        actions.andExpect(status().isOk());

        // verify the book does not exist any more
        actions = this.mockMvc.perform(get("/books/" + createdBook.getId()));
        // verify the response is not found
        actions.andExpect(status().is(404));
    }

    @Test
    public void testDELETE_DeleteBookThatDoesNotExist() throws Exception {
        // perform the DELETE request
        ResultActions actions = this.mockMvc.perform(delete("/books/99").contentType(
                MediaType.APPLICATION_JSON));

        // verify the response is not found
        actions.andExpect(status().is(404));
    }

}