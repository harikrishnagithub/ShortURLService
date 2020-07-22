package com.company.shortenurl.shortenurl.service;

import com.company.shortenurl.shortenurl.exceptions.InvalidHashTypeException;
import com.company.shortenurl.shortenurl.exceptions.NotImplementedException;
import com.company.shortenurl.shortenurl.hash.HashFunctionFactory;
import com.company.shortenurl.shortenurl.hash.MurmurHash3Implementation;
import com.company.shortenurl.shortenurl.model.BulkUrlRequest;
import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.model.JobStatus;
import com.company.shortenurl.shortenurl.model.UrlResource;
import com.company.shortenurl.shortenurl.queue.MessagePublisherImpl;
import com.company.shortenurl.shortenurl.repository.BulkUrlProcessingJobRepository;
import com.company.shortenurl.shortenurl.repository.UrlShorteningRepository;
import com.company.shortenurl.shortenurl.utils.UrlUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class TU_ShortenUrlGeneratorServiceImpl {

    private static final String JOBID = "9999l";
    final String SHORTURL = "abcd123";
    final String URL = "https://www.google.com/";

    @InjectMocks
    ShortenUrlGeneratorServiceImpl shortenUrlGeneratorServiceImpl;

    @Mock
    HashFunctionFactory hashFunctionFactory;
    @Mock
    MessagePublisherImpl messagePublisherImpl;

    @Mock
    UrlShorteningRepository URLShorteningRepository;

    @Mock
    BulkUrlProcessingJobRepository bulkUrlProcessingJobRepository;

    @BeforeEach
    void setUp() {
        hashFunctionFactory.setHashType("MURMUR3");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getOriginalUrl() {
        UrlResource resource = getUrlResource();
        Mockito.when(URLShorteningRepository.find(Mockito.anyString())).thenReturn(resource);

        shortenUrlGeneratorServiceImpl.getOriginalUrl(SHORTURL);
        assertEquals(URL, resource.getOriginalUrl());
        assertEquals(SHORTURL, resource.getShortUrl());
    }

    private UrlResource getUrlResource() {
        UrlResource resource= new UrlResource();
        resource.setOriginalUrl(URL);
        resource.setShortUrl(SHORTURL);
        return resource;
    }

    @Test
    void processBulkUrls() throws IOException {
        BulkUrlRequest bulkurl = new BulkUrlRequest();
        bulkurl.setName("Test");
        bulkurl.setFile(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));
        Mockito.when(bulkUrlProcessingJobRepository.find(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(messagePublisherImpl).publish(Mockito.anyString());
        Mockito.doNothing().when(bulkUrlProcessingJobRepository).add(Mockito.any());
        JobDefinition jobDefinition = shortenUrlGeneratorServiceImpl.processBulkUrls(bulkurl);
        assertTrue(jobDefinition.getJobId() != null);
        assertEquals(JobStatus.QUEUED,jobDefinition.getStatus());
    }

    @Test
    void findAll() {
    }

    @Test
    void getAllActiveJobs() {
    }

    @Test
    void createShortUrl() throws InvalidHashTypeException, NotImplementedException {
        UrlResource resource = getUrlResource();
        Mockito.when(URLShorteningRepository.find(Mockito.anyString())).thenReturn(null);
        Mockito.when(hashFunctionFactory.getHashGeneratorFactory()).thenReturn(new MurmurHash3Implementation());
        shortenUrlGeneratorServiceImpl.createShortUrl(URL);
        assertEquals(URL, resource.getOriginalUrl());
        assertEquals(SHORTURL, resource.getShortUrl());

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