package com.company.shortenurl.shortenurl.queue;

/**
 * Interface for publish messages to queue.
 */
public interface MessagePublisher {
    void publish(String message);
}
