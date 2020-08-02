package com.sparsis.libraryapi.service;

import com.sparsis.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {
    Book save(Book book);
    Optional<Book> findById(Long id);
    void delete(Long id);
    Book update(Book book);
}
