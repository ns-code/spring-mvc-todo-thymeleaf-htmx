package dev.thymeleafhtmx.data.model;

import lombok.Data;

// public record Todo(Integer id, String title, Category category) implements Comparable<Todo> {

//     @Override
//     public int compareTo(Todo o) {
//         return this.id().compareTo(o.id());
//     }
// }

@Data
public class Todo {
    private Integer id;
    private String title;
    private Category category;

    public Todo(Integer id, String title, Category category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }
}