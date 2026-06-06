package dev.thymeleafhtmx.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import dev.thymeleafhtmx.data.TodosDB;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @BeforeEach
    void initTodosDB() {
        TodosDB.initTodos();
    }    

    @Test
    void testHomePageLoads() throws Exception {
        MvcResult redirect = mockMvcTester.perform(get("/")).getMvcResult();

        assertThat(redirect.getResponse().getStatus()).isEqualTo(200);
        assertThat(redirect.getResponse().getContentAsString()).contains("/todos");
    }    
    
    @Test
    void testTodosPageLoads() throws Exception {

        MvcResult redirect = mockMvcTester.perform(get("/todos")).getMvcResult();

        assertThat(redirect.getResponse().getStatus()).isEqualTo(200);
        assertThat(redirect.getResponse().getContentAsString()).contains("Usage", "Categories", "Add a Todo");
    }    
    
    @Test
    void testCategoriesDropdownLoadsCorrectTodos() throws Exception {

        MvcResult redirect = mockMvcTester.perform(get("/todos/selectedCategory?categoryId=1")).getMvcResult();

        assertThat(redirect.getResponse().getStatus()).isEqualTo(200);
        assertThat(redirect.getResponse().getContentAsString()).contains("Read a book", "Visit book club");
    }     

    @Test
    void testAddTodoItemSucceeds() throws Exception {

        MvcResult redirect = mockMvcTester.perform(post("/todos").content("x-www-form-urlencoded")
            .param("new-todo", "My new todo").param("categoryId", "1")).getMvcResult();

        assertThat(redirect.getResponse().getStatus()).isEqualTo(200);
        assertThat(redirect.getResponse().getContentAsString()).contains("My new todo");
    }     

    @Test
    void testEditAndSaveTodoItemSucceeds() throws Exception {

        MvcResult res = mockMvcTester.perform(get("/todos/edit-title/1")).getMvcResult();
        assertThat(res.getResponse().getStatus()).isEqualTo(200);

        res = mockMvcTester.perform(post("/todos/update-title/5").content("x-www-form-urlencoded")
            .param("title", "My new updated todo")).getMvcResult();

        assertThat(res.getResponse().getStatus()).isEqualTo(200);
        assertThat(res.getResponse().getContentAsString()).contains("My new updated todo");
    }   
    
    @Test
    void testDeleteTodoItemSucceeds() {

        MvcResult redirect = mockMvcTester.perform(delete("/todos/2"))
            .getMvcResult();

        assertThat(redirect.getResponse().getStatus()).isEqualTo(200);
    }     
}
