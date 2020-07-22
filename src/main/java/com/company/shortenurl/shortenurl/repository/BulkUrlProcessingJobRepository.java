package com.company.shortenurl.shortenurl.repository;

import com.company.shortenurl.shortenurl.model.JobDefinition;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface BulkUrlProcessingJobRepository {

    /**
     * Return all Jobs
     */
    Map<String, JobDefinition> findAll();

    /**
     * Return all active Jobs
     */
    List<JobDefinition> findActiveJobs();

    void update(final JobDefinition job);

    /**
     * Add key-value pair to Redis.
     */
    void add(JobDefinition jobDefinition);

    /**
     * Delete a key-value pair in Redis.
     */
    void deleteJob(final String id);

    /**
     * find a Job Definition
     */
    JobDefinition find(String id);

}
