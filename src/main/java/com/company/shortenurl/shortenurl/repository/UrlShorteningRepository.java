package com.company.shortenurl.shortenurl.repository;

import com.company.shortenurl.shortenurl.model.UrlResource;

import java.util.List;
import java.util.Map;

/**
 * This class is for store the single shorten URL.
 */
public interface UrlShorteningRepository {

    /**
     * Return all Shorten URLS
     */
    Map<String, UrlResource> findAll();

    /**
     * Methode to fetch shorten URLs with offset and limit.
     * @param offset
     * @param limit
     * @return
     */
    List<UrlResource> findAllWithLimit(Integer offset, Integer limit);

    /**
     * Add key-value pair to Redis.
     */
    void add(UrlResource urlResource);

    /**
     *
     * @param urlRecourseList
     */
    void addAll(final List<UrlResource> urlRecourseList);

    /**
     * Delete a key-value pair in Redis.
     */
    void delete(String id);

    /**
     * find a movie
     */
    UrlResource find(String id);

}
