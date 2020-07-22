package com.company.shortenurl.shortenurl.service;

/**
 * Interface for Async File Processing Service.
 */
public interface AsyncFileProcessingService {
    /**
     * Method to start processing the job.
     * @param jobId
     * @throws Exception
     */
    public void process(String jobId) throws Exception;
}
