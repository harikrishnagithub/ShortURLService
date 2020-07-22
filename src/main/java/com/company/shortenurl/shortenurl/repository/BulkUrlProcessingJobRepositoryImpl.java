package com.company.shortenurl.shortenurl.repository;

import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class BulkUrlProcessingJobRepositoryImpl implements BulkUrlProcessingJobRepository {

    private static final String KEY = "JOBDEFINATION";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, JobDefinition> hashOperations;

    @Autowired
    public BulkUrlProcessingJobRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void add(final JobDefinition jobDefinition) {
        hashOperations.put(KEY, jobDefinition.getJobId(), jobDefinition);
    }

    public void delete(final String id) {
        hashOperations.delete(KEY, id);
    }

    public void update(final JobDefinition job) {
        hashOperations.put(KEY, job.getJobId(), job);
    }

    public void deleteJob(final String id) {
        hashOperations.delete(KEY, id);

    }

    public JobDefinition find(final String id) {
        return hashOperations.get(KEY, id);
    }

    public Map<String, JobDefinition> findAll() {
        return hashOperations.entries(KEY);
    }

    /**
     * Return all Jobs
     */
    @Override
    public List<JobDefinition> findActiveJobs() {
        return hashOperations.values(KEY).stream().filter(e -> e.getStatus() == JobStatus.INPROGRESS).collect(Collectors.toList());
    }


}
