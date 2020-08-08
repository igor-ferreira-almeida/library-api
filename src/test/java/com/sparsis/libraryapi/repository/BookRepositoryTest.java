package com.sparsis.libraryapi.repository;

import com.sparsis.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ActiveProfiles
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @DisplayName("Exists By ISBN - Success")
    @Test
    void existsByIsbnTrueTest() {
        String isbn = "001";
        Book book = createNewBook();

        entityManager.persist(book);
        boolean hasISBN = repository.existsByIsbn(isbn);

        Assertions.assertThat(hasISBN).isTrue();
    }

    @DisplayName("Exists By ISBN - Not Found")
    @Test
    void existsByIsbnFalseTest() {
        String isbn = "002";
        Book book = createNewBook();

        entityManager.persist(book);
        boolean hasISBN = repository.existsByIsbn(isbn);

        Assertions.assertThat(hasISBN).isFalse();
    }

    @DisplayName("Find By ID - Success")
    @Test
    void findByIdTest() {
        Book book = createNewBook();

        entityManager.persist(book);
        Optional<Book> optionalBook = repository.findById(1L);
        Book foundedBook = optionalBook.get();

        Assertions.assertThat(optionalBook.isPresent()).isTrue();
        Assertions.assertThat(foundedBook.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundedBook.getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @DisplayName("Find By ID - Not Found")
    @Test
    void findByIdNotFoundTest() {
        Optional<Book> optionalBook = repository.findById(1L);
        Assertions.assertThat(optionalBook.isPresent()).isFalse();
    }

    @DisplayName("Save Book - Success")
    @Test
    void saveBookSuccessTest() {
        Book book = createNewBook();
        Book savedBook = entityManager.persist(book);
        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @DisplayName("Update Book - Success")
    @Test
    void updateBookSuccessTest() {
        Book book = createNewBook();
        entityManager.persist(book);
        Book savedBook = entityManager.find(Book.class, 1L);

        Book updateBook = savedBook;
        updateBook.setTitle("title2");
        updateBook.setAuthor("author2");

        Book updatedBook = entityManager.merge(updateBook);

        Assertions.assertThat(updatedBook.getId()).isEqualTo(savedBook.getId());
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo(updateBook.getTitle());
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(updateBook.getAuthor());
    }

    @DisplayName("Delete Book - Success")
    @Test
    void deleteBookSuccessTest() {
        Book book = createNewBook();
        entityManager.persist(book);
        Book savedBook = entityManager.find(Book.class, 1L);

        entityManager.remove(savedBook);
        Book foundBook = entityManager.find(Book.class, book.getId());

        Assertions.assertThat(foundBook).isNull();
    }

    private Book createNewBook() {
        return Book.builder().title("title1").author("author1").isbn("001").build();
    }
}
