package com.sparsis.libraryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparsis.libraryapi.api.dto.BookDTO;
import com.sparsis.libraryapi.exception.BusinessException;
import com.sparsis.libraryapi.model.entity.Book;
import com.sparsis.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @DisplayName("Create book with success")
    @Test
    public void createBookTest() throws Exception {
        BookDTO bookDTO = createNewBookDTO();
        Book book = Book.builder().id(1L).title("title1").author("author1").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(book);
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

            mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                    .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                    .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                    .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @DisplayName("Create book with empty attributes")
    @Test
    public void createBookWithEmptyAttr() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @DisplayName("Create book with duplicated ISBN")
    @Test
    void createBookWithDuplicatedIsbn() throws Exception {
        BookDTO bookDTO = createNewBookDTO();
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException("Duplicated ISBN"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Duplicated ISBN"));
    }

    @DisplayName("Get Book detail with exists book")
    @Test
    void getBookDetailWithExistsBook() throws Exception {
        Long id = 1L;
        Book book = Book.builder().id(id).title("title1").author("author1").isbn("001").build();
        BDDMockito.given(service.findById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBookDTO().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBookDTO().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBookDTO().getIsbn()));
    }

    @DisplayName("Get Book detail not exists book")
    @Test
    void getBookDetailNotExistsBook() throws Exception {
        Long id = 1L;
        BDDMockito.given(service.findById(id)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Delete Book")
    @Test
    void deleteBook() throws Exception {
        Long id = 1L;
        Book book = Book.builder().id(id).build();
        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @DisplayName("Delete book that not exists")
    @Test
    void deleteBookThatNotExists() throws Exception {
        Long id = 1L;
        Book book = Book.builder().id(id).build();

        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Update book")
    @Test
    void updateBook() throws Exception {
        Long id = 1L;
        BookDTO bookDTO = BookDTO.builder().title("title2").author("author2").build();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        Book book = Book.builder().id(1L).title("title1").author("author1").isbn("001").build();
        BDDMockito.given(service.findById(id)).willReturn(Optional.of(book));

        Book updatedBook = Book.builder().id(book.getId()).title(bookDTO.getTitle()).author(bookDTO.getAuthor()).isbn(book.getIsbn()).build();
        BDDMockito.given(service.update(Mockito.any(Book.class))).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()));
    }

    @DisplayName("Update book that not exists")
    @Test
    void updateBookThatNotExists() throws Exception {
        BookDTO bookDTO = BookDTO.builder().title("title2").author("author2").build();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @DisplayName("Find By - Success")
    @Test
    void findByTest() throws Exception {
        Long id = 1L;
        Book book = Book.builder().id(id).title("title1").author("author1").isbn("001").build();
        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class))).willReturn(new PageImpl<>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryParam = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryParam))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBookDTO() {
        return BookDTO.builder().title("title1").author("author1").isbn("001").build();
    }
}
