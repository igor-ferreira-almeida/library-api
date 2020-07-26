package com.sparsis.libraryapi.service;

import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new BookServiceImpl(repository);
    }

    @DisplayName("save book with success")
    @Test
    public void saveWithSuccess() {
        Book book = Book.builder().title("title1").author("author1").isbn("isbn1").build();
        Book returnOfRepository = Book.builder().id(1L).title(book.getTitle()).author(book.getAuthor()).isbn(book.getIsbn()).build();

        Mockito.when(repository.save(book)).thenReturn(returnOfRepository);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("title1");
        assertThat(savedBook.getAuthor()).isEqualTo("author1");
        assertThat(savedBook.getIsbn()).isEqualTo("isbn1");
    }
}
