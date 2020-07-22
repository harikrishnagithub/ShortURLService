package com.company.shortenurl.shortenurl.repository;

import com.company.shortenurl.shortenurl.model.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UrlShorteningRepositoryImpl implements UrlShorteningRepository {

    private static final String KEY = "URLMAPPER";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, UrlResource> hashOperations;

    @Autowired
    public UrlShorteningRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Method to store the URLResource in database.
     * @param urlResourceEntity
     */
    public void add(final UrlResource urlResourceEntity) {
        hashOperations.put(KEY, urlResourceEntity.getShortUrl(), urlResourceEntity);
    }

    /**
     * Method to store bulk URL resources
     * @param urlRecourseList
     */
    public void addAll(final List<UrlResource> urlRecourseList) {
        urlRecourseList.stream().forEach(urlRecourse -> {
            hashOperations.put(KEY, urlRecourse.getShortUrl(), urlRecourse);
        });
    }

    /**
     * Method to delete the Shorten url.
     * @param id
     */
    public void delete(final String id) {
        hashOperations.delete(KEY, String.valueOf(id));
    }

    public UrlResource find(final String id) {
        return hashOperations.get(KEY, String.valueOf(id));
    }


    @Override
    public Map<String, UrlResource> findAll() {
        return hashOperations.entries(KEY);
    }

    /**
     * Return all Shorten URLS
     *
     * @param offset
     * @param limit
     */
    public List<UrlResource> findAllWithLimit(Integer offset, Integer limit) {
        Map<String, UrlResource> map = hashOperations.entries(KEY);
        return map.values().stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

}
