package org.library.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.library.WebApplicationInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.ServletContextConfiguration.class,
        WebApplicationInitializer.RootContextConfiguration.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class BooksControllerTest {

    @Autowired
    private BooksController bookController;
    private MockMvc mockMvc;

    @Before
    public void setupBefore() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testFindAllBooks() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/books"));
        actions.andDo(print());
    }

    @Test
    public void testFindSingleBook() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/books/1"));
        actions.andDo(print());
        actions.andExpect(status().isOk());
        actions.andExpect(content().string(
                "{\"id\":1,"
                        + "\"title\":\"The Elegant Universe: Superstrings, Hidden Dimensions, "
                        + "and the Quest for the Ultimate Theory\","
                        + "\"author\":\"Brian Greene\",\"new\":false}"));

    }

}