package com.example.springBoot_Redis.controller;

import java.util.List;

import com.example.springBoot_Redis.dto.CreateBookDto;
import com.example.springBoot_Redis.entity.Book;
import com.example.springBoot_Redis.service.BookService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookContorller {

    @Setter(onMethod_ = @Autowired)
    private BookService bookService;

    @PostMapping(path = "add")
    public ResponseEntity<Book> addBook(@RequestBody CreateBookDto bookDto) {
        Book book = bookService.create(bookDto);
        return ResponseEntity.ok().body(book);
    }

    @GetMapping(path = "cacheableAll")
    public ResponseEntity<List<Book>> cacheableFindAll() {
        return ResponseEntity.ok().body(bookService.cacheableFindAll());
    }

    @GetMapping(path = "redisAll")
    public ResponseEntity<List<Book>> redisFindAll() {
        return ResponseEntity.ok().body(bookService.redisFindAll());
    }


    @GetMapping(path = "clearAll")
    public ResponseEntity<?> clearAll() {
        bookService.clearCache();
        return ResponseEntity.ok().body(null);
    }

    @PostMapping(path = "cacheableFind/{id}")
    public ResponseEntity<Book> cacheableFindById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(bookService.cacheableFindById(id));
    }

    @PostMapping(path = "redisFind/{id}")
    public ResponseEntity<Book> redisFindById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok().body(bookService.redisFindById(id));
    }

    @PostMapping(path = "clearFind/{id}")
    public ResponseEntity<?> clearById(@PathVariable(value = "id") Long id) {
        bookService.clearId(id);
        return ResponseEntity.ok().body(null);
    }
}
