package com.sparsis.libraryapi.service;

import com.sparsis.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book book);
}
