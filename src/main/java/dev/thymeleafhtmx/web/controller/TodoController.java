package dev.thymeleafhtmx.web.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.FragmentsRendering;

import dev.thymeleafhtmx.data.TodosDB;
import dev.thymeleafhtmx.data.model.Todo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/todos")
class TodoController {

    private Set<Todo> todos = Collections.synchronizedSortedSet(new TreeSet<>(Comparator.comparingInt(Todo::id)));

    TodoController() {
        for (var t : "read a book, go to the gym, learn HATEOAS".split(","))
            this.todos.add(TodosDB.todo(t));
    }

    @GetMapping
    String todos(Model model) {
        model.addAttribute("todos", this.todos);
        return "todos";
    }

    @GetMapping("/edit-title/{id}")
    public String editTitle(@PathVariable Integer id, Model model) {
        model.addAttribute("todo", todos.stream().filter(t -> t.id().equals(id)).findFirst().orElseThrow());

        return "fragments/todo-title-edit :: edit";
    }

    @PostMapping("/update-title/{id}")
    public String updateTitle(@PathVariable Integer id,
            @ModelAttribute("title") String title,
            Model model) {

        // update todos set with the new title, then return the updated todo to be
        // rendered in the edit form
        todos = todos.stream().map(t -> {
            if (t.id().equals(id)) {
                return new Todo(t.id(), title);
            } else {
                return t;
            }
        }).collect(Collectors.toSet());

        log.info(">> todos: {}", todos);
        Todo todo = todos.stream().filter(t -> t.id().equals(id)).findFirst()
                .orElseThrow();
        model.addAttribute("todo", todo);
        return "todos :: display";
    }

    @PostMapping
    FragmentsRendering add(@RequestParam("new-todo") String newTodo,
            Model model) {

        this.todos.add(TodosDB.todo(newTodo));
        // model.addAttribute("todos",
        //         this.todos.stream()
        //                 .sorted(Comparator.comparingInt(Todo::id))
        //                 .toList());
        model.addAttribute("todos", todos);

        return FragmentsRendering
                .fragment("todos :: todos-form") // OOB swap
                .fragment("todos :: todos") // Another OOB swap
                .build();
    }

    @ResponseBody
    @DeleteMapping(produces = MediaType.TEXT_HTML_VALUE, path = "/{todoId}")
    String delete(@PathVariable Integer todoId) {
        todos.removeIf(t -> t.id().equals(todoId));
        return "";
    }
}
