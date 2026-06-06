package dev.thymeleafhtmx.web.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.FragmentsRendering;

import dev.thymeleafhtmx.data.TodosDB;
import dev.thymeleafhtmx.data.model.Category;
import dev.thymeleafhtmx.data.model.Todo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/todos")
class TodoController {

    public TodoController() {
        TodosDB.initTodos();
    }

    @GetMapping
    String todos(Model model) {
        model.addAttribute("categories", TodosDB.getCategories());
        model.addAttribute("todos", TodosDB.getTodosForCategoryId(1));
        model.addAttribute("selectedCategoryId", 1);
        return "todos";
    }

    @GetMapping("/edit-title/{id}")
    public String editTitle(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("todo", TodosDB.getTodoForId(id));

        return "fragments/todo-frags/edit-frag :: edit";
    }

    @PostMapping("/update-title/{id}")
    public String updateTitle(@PathVariable("id") Integer id,
            @RequestParam("title") String title,
            Model model) {

        // update todos set with the new title, then return the updated todo to be
        // rendered in the edit form
        Todo updatedTodo = TodosDB.updateTodoForId(id, title);

        model.addAttribute("todo", updatedTodo);
        return "todos :: display";
    }

    @PostMapping
    FragmentsRendering add(@RequestParam("new-todo") String newTodo, @RequestParam("categoryId") Integer categoryId,
            Model model) {
        Category category = TodosDB.getCategoryById(categoryId);
        TodosDB.addTodo(TodosDB.todo(newTodo, category));
        model.addAttribute("todos", TodosDB.getTodosForCategoryId(categoryId));
        model.addAttribute("newTodo", "");
        model.addAttribute("selectedCategoryId", categoryId);
        return FragmentsRendering
                .fragment("fragments/todo-frags/list-frag :: list-frag")
                .fragment("todos :: add-frag")
                .build();
    }

    // @ResponseBody
    @DeleteMapping(produces = MediaType.TEXT_HTML_VALUE, path = "/{todoId}")
    FragmentsRendering delete(@PathVariable("todoId") Integer todoId, Model model) {
        Todo todo = TodosDB.getTodoForId(todoId);
        Integer selectedCategoryId = todo.getCategory().getId();
        TodosDB.deleteTodoForId(todoId);
        log.info(">> todos: {}", TodosDB.getTodosForCategoryId(todo.getCategory().getId()));
        model.addAttribute("todos", TodosDB.getTodosForCategoryId(selectedCategoryId));
        return FragmentsRendering
                .fragment("fragments/todo-frags/list-frag :: list-frag")
                .build();
    }

    @GetMapping("/selectedCategory")
    public FragmentsRendering selectedCategory(@RequestParam("categoryId") Integer categoryId, Model model) {
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("todos", TodosDB.getTodosForCategoryId(categoryId));
        return FragmentsRendering
                .fragment("fragments/todo-frags/list-frag :: list-frag")
                .fragment("fragments/todo-frags/add-frag :: add-frag")
                .build();
    }    
}
