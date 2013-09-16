package org.library.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.context.WebApplicationContext;

/**
 * A test of the {@link BooksController} that's more of an integration test than a unit test,
 * because of it's use of spring-test to load up the context prior to running the test. This test
 * focuses on the "/books" REST API endpoint.
 * 
 * @author dylants
 * 
 */
// This annotation is required to load up a test context and take advantage of the
// many Spring test resources. In the end, it's still a JUnit test, but this annotation
// links up Spring's test context without having to use Spring's JUnit classes.
@RunWith(SpringJUnit4ClassRunner.class)
// Informs Spring to load up these configuration classes prior to running the test
@ContextConfiguration(classes = {WebApplicationInitializer.RootContextConfiguration.class,
        WebApplicationInitializer.ServletContextConfiguration.class})
// Specifies the active profile for the configuration classes
@ActiveProfiles("development")
// Specifies that the application context should be a WebApplicationContext
@WebAppConfiguration
public class BooksControllerTest {

    // Used to convert JSON to Java, and Java to JSON
    private static ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BooksController bookController;

    private MockMvc mockMvc;

    @BeforeClass
    public static void setupBeforeClass() {
        objectMapper = new ObjectMapper();
    }

    @Before
    public void setupBefore() {
        // Create our MockMvc utilizing the current WebApplicationContext. This will give
        // us access to all the endpoints configured within our application.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    /**
     * Tests the GET action for all {@link Book}s
     * 
     * @throws Exception
     */
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
        List<Book> books = this.convertToBooks(content);

        // verify there are books
        Assert.assertNotNull("books must exist", books);
        Assert.assertTrue("there must be at least one book", books.size() > 0);
    }

    /**
     * Tests the GET action for a single {@link Book} that was preloaded in the database prior to
     * running any tests (in the "development" profile)
     * 
     * @throws Exception
     */
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
        Book book = this.convertToBook(content);

        // verify the book details
        Assert.assertEquals("id must match", (long) 1L, (long) book.getId());
        Assert.assertEquals("title must match", "The Elegant Universe: Superstrings, Hidden "
                + "Dimensions, and the Quest for the Ultimate Theory", book.getTitle());
        Assert.assertEquals("author must match", "Brian Greene", book.getAuthor());
    }

    /**
     * Tests the error case of GET on a single {@link Book} that does not exist
     * 
     * @throws Exception
     */
    @Test
    public void testGET_BookThatDoesNotExist() throws Exception {
        String bookId = "99999999";
        // perform the GET request
        ResultActions actions = this.mockMvc.perform(get("/books/" + bookId));

        // verify the response is not found
        actions.andExpect(status().isNotFound());
    }

    /**
     * Tests the POST action to create a {@link Book}
     * 
     * @throws Exception
     */
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

        // verify the response is CREATED
        actions.andExpect(status().isCreated());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = this.convertToBook(content);

        // verify the book details
        Assert.assertTrue("id must exist", createdBook.getId() != null);
        Assert.assertEquals("title must match", title, createdBook.getTitle());
        Assert.assertEquals("author must match", author, createdBook.getAuthor());
    }

    /**
     * Tests the PUT action by first creating a {@link Book}, and then updating this {@link Book}
     * 
     * @throws Exception
     */
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

        // verify the response is CREATED
        actions.andExpect(status().isCreated());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = this.convertToBook(content);

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
        Book updatedBook = this.convertToBook(content);

        // verify the book details
        Assert.assertEquals("id must match", createdBook.getId(), updatedBook.getId());
        Assert.assertEquals("title must match", updatedTitle, updatedBook.getTitle());
        Assert.assertEquals("author must match", createdBook.getAuthor(), updatedBook.getAuthor());
    }

    /**
     * Tests the error case of the PUT action by attempting to update a {@link Book} that does not
     * exist.
     * 
     * @throws Exception
     */
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

    /**
     * Tests the DELETE action by creating a {@link Book} and then deleting it, verifying it is no
     * longer there.
     * 
     * @throws Exception
     */
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

        // verify the response is CREATED
        actions.andExpect(status().isCreated());
        MvcResult result = actions.andReturn();
        Assert.assertNotNull("result must not be null", result);
        String content = result.getResponse().getContentAsString();
        Assert.assertTrue("content must exist", StringUtils.isNotBlank(content));

        // convert the content in the response to a book
        Book createdBook = this.convertToBook(content);

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

    /**
     * Tests the error case of DELETE by attempting to delete a {@link Book} that does not exist
     * 
     * @throws Exception
     */
    @Test
    public void testDELETE_DeleteBookThatDoesNotExist() throws Exception {
        // perform the DELETE request
        ResultActions actions = this.mockMvc.perform(delete("/books/99").contentType(
                MediaType.APPLICATION_JSON));

        // verify the response is not found
        actions.andExpect(status().is(404));
    }

    /**
     * Internal method that converts a JSON {@link String} of a {@link Book} to a {@link Book}
     * object
     * 
     * @param content
     *            A {@link Book} in JSON {@link String} format
     * @return The {@link Book} object
     * @throws Exception
     */
    protected Book convertToBook(String content) throws Exception {
        // hopefully temporary, but convert to Object to remove links, then to Book
        @SuppressWarnings("unchecked")
        Map<String, String> resource = objectMapper.readValue(content, Map.class);
        resource.remove("links");
        Book book = objectMapper.readValue(objectMapper.writeValueAsString(resource), Book.class);
        return book;
    }

    /**
     * Internal method that converts a JSON list of {@link Book}s into {@link Book} objects
     * 
     * @param content
     *            A list of {@link Book}s in JSON {@link String} format
     * @return The {@link List} of {@link Book}s
     * @throws Exception
     */
    protected List<Book> convertToBooks(String content) throws Exception {
        // hopefully temporary, but pull just the content to convert to books
        @SuppressWarnings("unchecked")
        Map<String, ?> resource = objectMapper.readValue(content, Map.class);
        @SuppressWarnings("unchecked")
        List<? extends Map<String, String>> innerContentList = (List<? extends Map<String, String>>) resource
                .get("content");
        List<Book> books = new ArrayList<>();
        for (Map<String, String> innerContent : innerContentList) {
            // hopefully temporary, remove links before converting to Book
            innerContent.remove("links");
            books.add(objectMapper.readValue(objectMapper.writeValueAsString(innerContent),
                    Book.class));
        }
        return books;
    }

}