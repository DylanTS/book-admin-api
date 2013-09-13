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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.RootContextConfiguration.class,
        WebApplicationInitializer.ServletContextConfiguration.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class BooksControllerTest {

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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
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
        List<Book> books = this.convertToBooks(content);

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
        Book book = this.convertToBook(content);

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

    @Test
    public void testDELETE_DeleteBookThatDoesNotExist() throws Exception {
        // perform the DELETE request
        ResultActions actions = this.mockMvc.perform(delete("/books/99").contentType(
                MediaType.APPLICATION_JSON));

        // verify the response is not found
        actions.andExpect(status().is(404));
    }

    protected Book convertToBook(String content) throws Exception {
        // hopefully temporary, but convert to Object to remove links, then to Book
        @SuppressWarnings("unchecked")
        Map<String, String> resource = objectMapper.readValue(content, Map.class);
        resource.remove("links");
        Book book = objectMapper.readValue(objectMapper.writeValueAsString(resource), Book.class);
        return book;
    }

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