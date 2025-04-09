package com.example.springBoot_Redis.service;

import java.util.List;
import java.util.Optional;

import com.example.springBoot_Redis.cache.CacheProxy;
import com.example.springBoot_Redis.dto.CreateBookDto;
import com.example.springBoot_Redis.entity.Book;
import com.example.springBoot_Redis.repository.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "bookService")
public class BookService {

    @Setter(onMethod_ = @Autowired)
    private BookRepository bookRepository;

    @Setter(onMethod_ = @Autowired)
    private CacheProxy cacheProxy;

    public Book create(CreateBookDto bookDto) {
        Book book = new Book()
                .setTitle(bookDto.getTitle())
                .setAuthor(bookDto.getAuthor())
                .setCategory(bookDto.getCategory())
                .setPrice(Double.valueOf(bookDto.getPrice()));
        book = bookRepository.save(book);
        return book;
    }

    // 使用 @Cacheable
    @Cacheable(value = "cacheableFindById", key = "#id")
    public Book cacheableFindById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 使用 RedisUtil
    public Book redisFindById(Long id) {
        return cacheProxy.getOneCache(BookService.class, "redisFindById", Book.class, () -> bookRepository.findById(id)).orElse(null);
    }

    @CacheEvict(value = "findById", key = "#id")
    public void clearId(Long id) {
    }

    @Cacheable(value = "cacheableFindAll")
    public List<Book> cacheableFindAll() {
        return bookRepository.findAll();
    }

    public List<Book> redisFindAll() {
        return cacheProxy.getListCache(BookService.class, "redisFindAll", Book.class, () -> bookRepository.findAll());
    }

    @CacheEvict(value = "findAll", allEntries = true)
    public void clearCache() {
    }

}
