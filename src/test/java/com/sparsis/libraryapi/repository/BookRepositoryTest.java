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

@ActiveProfiles
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @DisplayName("Return true with exists ISBN")
    @Test
    void returnTrueWithExistIsbn() {
        String isbn = "001";
        Book book = createNewBook();

        entityManager.persist(book);
        boolean hasISBN = repository.existsByIsbn(isbn);

        Assertions.assertThat(hasISBN).isTrue();
    }

    @DisplayName("Return false with exists ISBN")
    @Test
    void returnFalseWithExistIsbn() {
        String isbn = "002";
        Book book = createNewBook();

        entityManager.persist(book);
        boolean hasISBN = repository.existsByIsbn(isbn);

        Assertions.assertThat(hasISBN).isFalse();
    }

    private Book createNewBook() {
        return Book.builder().title("title1").author("author1").isbn("001").build();
    }
}
