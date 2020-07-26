package com.sparsis.libraryapi.repository;

import com.sparsis.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
