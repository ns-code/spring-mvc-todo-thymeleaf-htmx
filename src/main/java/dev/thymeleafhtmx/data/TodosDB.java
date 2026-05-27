package dev.thymeleafhtmx.data;

import java.util.concurrent.atomic.AtomicInteger;

import dev.thymeleafhtmx.data.model.Todo;

public class TodosDB {
    private TodosDB() {
        // hide implicit public constructor
    }

    private static final AtomicInteger id = new AtomicInteger(0);

    public static Todo todo(String title) {
        return new Todo(id.incrementAndGet(), title);
    }
}
