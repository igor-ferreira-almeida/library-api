package com.sparsis.libraryapi.api.controller;

import com.sparsis.libraryapi.api.dto.BookDTO;
import com.sparsis.libraryapi.api.exception.ApiErrors;
import com.sparsis.libraryapi.exception.BusinessException;
import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RequestMapping("/api/books")
@RestController
public class BookController {

    @Autowired
    private BookService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedResource = service.save(book);
        BookDTO responseDTO = modelMapper.map(savedResource, BookDTO.class);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/books/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> find(@PathVariable Long id) {
        Optional<Book> bookOptional = service.findById(id);

        if(bookOptional.isPresent()) {
            BookDTO responseDTO = modelMapper.map(bookOptional.get(), BookDTO.class);
            return ResponseEntity.ok(responseDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        Optional<Book> bookOptional = service.findById(id);

        if(bookOptional.isPresent()) {
            Book book = bookOptional.get();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            Book updatedResource = service.update(book);

            BookDTO responseDTO = modelMapper.map(updatedResource, BookDTO.class);
            return ResponseEntity.ok().body(responseDTO);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Book> bookOptional = service.findById(id);

        if(bookOptional.isPresent()) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
