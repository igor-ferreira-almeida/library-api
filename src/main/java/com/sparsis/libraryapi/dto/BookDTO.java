package com.sparsis.libraryapi.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
