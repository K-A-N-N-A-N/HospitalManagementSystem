package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class QueueService {

    private final JmsTemplate jmsTemplate;

    public QueueService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendToQueue(String queueName, Object message) {
        try {
            jmsTemplate.convertAndSend(queueName, message);
            log.info("Sent to queue [{}]: {}", queueName, message);
        } catch (Exception e) {
            throw new SystemOperationException("Failed to send message to " + queueName, e);
        }
    }
}
