package com.company.shortenurl.shortenurl.service;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.InvalidJobIdentifierException;
import com.company.shortenurl.shortenurl.exceptions.InvalidShortUrlException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.JobStatus;
import com.company.shortenurl.shortenurl.repository.BulkUrlProcessingJobRepository;
import com.company.shortenurl.shortenurl.utils.UrlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  This class is Async File Processing Service is for processing the bulk URLS asynchronously.
 */
@Service
public class AsyncFileProcessingServiceImpl implements AsyncFileProcessingService {

    @Autowired
    BulkUrlProcessingJobRepository bulkURLProcessingJobRepository;
    @Autowired
    ShortenUrlGeneratorService shortenUrlGeneratorService;

    @Value("${application.custom.processslotsize}")
    private Integer counter;
    /**
     * Method to start processing the job.
     * @param jobId
     * @throws Exception
     */
    public void process(String jobId) {
        JobDefinition jobDefinition = bulkURLProcessingJobRepository.find(jobId);
        if (jobDefinition == null)
            throw new InvalidJobIdentifierException("Invalid Job identifier");
        jobDefinition.setStatus(JobStatus.INPROGRESS);
        bulkURLProcessingJobRepository.update(jobDefinition);
        String encodedString = jobDefinition.getFile();
        byte[] fileByteArray = UrlUtility.getDecodedByteArray(encodedString);
        File file = UrlUtility.writeByte(fileByteArray);
        List<String> result = new ArrayList<>();
        try {
            Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()));
            result = stream.collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        int skip = 0;
        while (result.size() >= skip) {
            List<String> list = result.stream().skip(skip).limit(counter).collect(Collectors.toList());
            try {
                shortenUrlGeneratorService.bulkGenerateShortenUrl(jobId, list);
            } catch (InvalidHashTypeException | NotImplementedException e) {
                throw new InvalidShortUrlException("Failed to generate bulk short URL for the given file.");
            }
            skip += counter;
        }
        jobDefinition = bulkURLProcessingJobRepository.find(jobDefinition.getJobId());
        jobDefinition.setStatus(JobStatus.COMPLETED);
        jobDefinition.setFile(null);
        bulkURLProcessingJobRepository.update(jobDefinition);
    }
    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
