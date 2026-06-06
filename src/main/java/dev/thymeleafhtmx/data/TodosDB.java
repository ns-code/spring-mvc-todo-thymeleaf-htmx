package dev.thymeleafhtmx.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import dev.thymeleafhtmx.data.model.Category;
import dev.thymeleafhtmx.data.model.Todo;

public class TodosDB {
    private TodosDB() {
        // hide implicit public constructor
    }

    private static final AtomicInteger id = new AtomicInteger(0);
    private static final List<Category> categories = List.of(
            new Category(1, "Learning"),
            new Category(2, "Health"));
    private static List<Todo> todos = Collections.synchronizedList(new ArrayList<>());

    public static Todo todo(String title, Category category) {
        return new Todo(id.incrementAndGet(), title, category);
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public static void initTodos() {
        // Initialization logic if needed
        todos.add(todo("Read a book", getCategoryById(1)));
        todos.add(todo("Visit book club", getCategoryById(1)));
        todos.add(todo("Go for a run", getCategoryById(2)));
        todos.add(todo("Go to the gym", getCategoryById(2)));
    }

    public static List<Todo> getTodos() {
        return todos;
    }

    public static Category getCategoryById(Integer id) {
        return categories.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow();
    }

    public static Todo getTodoForId(Integer id) {
        return todos.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow();
    }

    public static Todo updateTodoForId(Integer id, String updTitle) {
        Todo todo = todos.stream().filter(t -> t.getId().equals(id)).findFirst().orElseThrow();
        todo.setTitle(updTitle);
        return todo;
    }    

    public static List<Todo> getTodosForCategoryId(Integer id) {
        return todos.stream().filter(t -> t.getCategory().getId().equals(id)).toList();
    }

    public static void addTodo(Todo todo) {
        todos.add(todo);
    }

    public static void deleteTodoForId(Integer id) {
        todos.removeIf(t -> t.getId().equals(id));
    }   
}
