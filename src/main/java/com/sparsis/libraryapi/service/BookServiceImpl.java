package com.sparsis.libraryapi.service;

import com.sparsis.libraryapi.exception.BusinessException;
import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Duplicated ISBN");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
       return repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Book update(Book book) {
        return repository.save(book);
    }
}
