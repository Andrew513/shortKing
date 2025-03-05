package com.example.urlshortener.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.urlshortener.models.Url;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends MongoRepository<Url, String> {
    Optional<Url> findByShortUrl(String shortUrl);
    Optional<Url> findByLongUrl(String longUrl);
    List<Url> findByCreatedBy(String createdBy);
}