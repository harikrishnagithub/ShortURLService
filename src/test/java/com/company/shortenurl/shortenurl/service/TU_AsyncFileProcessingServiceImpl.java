package com.company.shortenurl.shortenurl.service;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.JobStatus;
import com.company.shortenurl.shortenurl.repository.BulkUrlProcessingJobRepository;
import com.company.shortenurl.shortenurl.utils.UrlUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        value={"application.custom.processslotsize=10",
                "spring.some.other.property=propertyValue"})
@PropertySource("classpath:application.properties ")
public class TU_AsyncFileProcessingServiceImpl {

    final String JOBID = "99999";
    final String URL = "https://www.google.com/";

    @InjectMocks
    AsyncFileProcessingServiceImpl asyncFileProcessingService;

    @Mock
    BulkUrlProcessingJobRepository bulkUrlProcessingJobRepository;
    @Mock
    ShortenUrlGeneratorServiceImpl shortenUrlGeneratorService;
    @BeforeEach
    void setUp() {
        asyncFileProcessingService.setCounter(10);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void process() throws InvalidHashTypeException, NotImplementedException {
        JobDefinition jobDefinition = createJobDefinition();

        Mockito.when(bulkUrlProcessingJobRepository.find(JOBID)).thenReturn(jobDefinition);
        Mockito.doNothing().when(bulkUrlProcessingJobRepository).update(jobDefinition);
        Mockito.doNothing().when(shortenUrlGeneratorService).bulkGenerateShortenUrl(Mockito.any(),Mockito.any());

        try {
            asyncFileProcessingService.process(JOBID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(JobStatus.COMPLETED, jobDefinition.getStatus());
    }

    private JobDefinition createJobDefinition() {
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setJobId(JOBID);
        try {

            Files.write(Paths.get("test.txt"), URL.getBytes());
            File file= new File("test.txt");
            byte[] encoded = Files.readAllBytes(Paths.get("test.txt"));
            jobDefinition.setFile(UrlUtility.getEncodedString(encoded));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jobDefinition.setStatus(JobStatus.QUEUED);
        return jobDefinition;
    }

}