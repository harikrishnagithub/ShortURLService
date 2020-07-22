package com.company.shortenurl.shortenurl.queue;

import com.company.shortenurl.shortenurl.model.JobDefinition;
import com.company.shortenurl.shortenurl.service.AsyncFileProcessingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Message subcriber works as a client for message queue. As soon as Message published
 * then this class will pick the message from queue and process further.
 */
@Component("messageSubscriber")
@Configurable
public class MessageSubscriber implements MessageListener {

    public static List<String> messageList = new ArrayList<String>();

    @Autowired
    AsyncFileProcessingService asyncFileProcessingService;

    /**
     * Methode for start process the message.
     * @param message
     * @param pattern
     */
    public void onMessage(final Message message, final byte[] pattern) {
        messageList.add(message.toString());
        ObjectMapper obj = new ObjectMapper();
        obj.configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, true);
        JobDefinition jobDefinition = null;
        try {
            jobDefinition = obj.readValue(message.toString(), JobDefinition.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(jobDefinition.toString());
            asyncFileProcessingService.process(jobDefinition.getJobId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end start " + messageList.size());
    }

}
