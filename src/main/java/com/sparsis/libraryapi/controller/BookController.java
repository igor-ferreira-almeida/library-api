package com.sparsis.libraryapi.controller;

import com.sparsis.libraryapi.dto.BookDTO;
import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequestMapping("/api/books")
@RestController
public class BookController {

    @Autowired
    private BookService service;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO bookDTO) {
        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedResource = service.save(book);
        BookDTO responseDTO = modelMapper.map(savedResource, BookDTO.class);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/books/{id}").buildAndExpand(responseDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

}
