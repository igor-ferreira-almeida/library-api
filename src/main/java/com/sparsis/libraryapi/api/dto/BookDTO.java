package com.sparsis.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
public class BookDTO {
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;
}
