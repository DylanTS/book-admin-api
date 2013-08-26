package org.library.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

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
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebApplicationInitializer.ServletContextConfiguration.class,
        WebApplicationInitializer.RootContextConfiguration.class})
@ActiveProfiles("development")
@WebAppConfiguration
public class BooksControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setupBefore() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.wac).build();
    }

    @Test
    public void testFindAllBooks() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/books").contextPath("/library").servletPath("/api"));
        actions.andDo(print());
    }

    @Test
    public void testAddAndFindBook() throws Exception {
        ResultActions actions = this.mockMvc.perform(get("/books/1"));
        actions.andDo(print());
        actions.andExpect(status().isOk());
        actions.andExpect(content().contentType("application/xml"));
        actions.andExpect(content().string("blah"));
    }

}