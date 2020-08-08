package com.sparsis.libraryapi.service;

import com.sparsis.libraryapi.exception.BusinessException;
import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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

    @DisplayName("Save Book - Success")
    @Test
    void saveSuccessTest() {
        Book book = createValidBook();
        Book returnOfRepository = Book.builder().id(1L).title(book.getTitle()).author(book.getAuthor()).isbn(book.getIsbn()).build();

        Mockito.when(repository.save(book)).thenReturn(returnOfRepository);

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("title1");
        assertThat(savedBook.getAuthor()).isEqualTo("author1");
        assertThat(savedBook.getIsbn()).isEqualTo("001");
    }

    @DisplayName("Save Book - Business Exception - Duplicated ISBN")
    @Test
    void saveBookWithDuplicatedIsbnTest() {
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(book.getIsbn())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Duplicated ISBN");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @DisplayName("Find By ID - Success")
    @Test
    void findByIdSuccessTest() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
        Optional<Book> optionalBook = service.findById(id);

        assertThat(optionalBook.isPresent()).isTrue();
        Book foundedBook = optionalBook.get();
        assertThat(foundedBook.getId()).isEqualTo(book.getId());
        assertThat(foundedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(foundedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundedBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundedBook).isEqualTo(book);
    }

    @DisplayName("Find By ID - Not Found")
    @Test
    void findByIdNotFoundTest() {
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Book> optionalBook = service.findById(id);

        assertThat(optionalBook.isPresent()).isFalse();
    }

    @DisplayName("Update - Success")
    @Test
    void updateSuccessTest() {
        Book book = createValidBook();
        book.setId(1L);
        Mockito.when(repository.save(book)).thenReturn(book);

        Book updatedBook = service.update(book);

        assertThat(updatedBook.getId()).isEqualTo(book.getId());
        assertThat(updatedBook).isEqualTo(book);
    }

    @DisplayName("Update - Illegal Argument Exception - Null Book")
    @Test
    void updateNullBookTest() {
        Book book = null;
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @DisplayName("Update - Illegal Argument Exception - Null Book ID")
    @Test
    void updateNullIDTest() {
        Book book = createValidBook();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @DisplayName("Delete - Success")
    @Test
    void deleteSuccessTest() {
        Long id = 1L;
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(id));
        Mockito.verify(repository, Mockito.times(1)).deleteById(id);
    }

    @DisplayName("Delete - Not Found")
    @Test
    void deleteNotFoundTest() {
        Long id = null;
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(id));
        Mockito.verify(repository, Mockito.never()).deleteById(id);
    }

    private Book createValidBook() {
        return Book.builder().title("title1").author("author1").isbn("001").build();
    }


}
