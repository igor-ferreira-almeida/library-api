package com.sparsis.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
