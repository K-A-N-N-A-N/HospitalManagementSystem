package com.hospitalmanagement.hospital_crud.service;

import com.hospitalmanagement.hospital_crud.exceptions.SystemOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    JmsTemplate jmsTemplate;

    @InjectMocks
    QueueService queueService;

    @Test
    @DisplayName("Should send message to queue using JmsTemplate")
    void sendToQueue_shouldSendMessage() {
        queueService.sendToQueue("test.queue", "payload");

        verify(jmsTemplate, times(1)).convertAndSend("test.queue", "payload");
    }

    @Test
    @DisplayName("Should throw SystemOperationException when sending fails")
    void sendToQueue_shouldThrowSystemOperationException_whenSendFails() {
        doThrow(new RuntimeException("broker down"))
                .when(jmsTemplate).convertAndSend("test.queue", "payload");

        assertThrows(SystemOperationException.class,
                () -> queueService.sendToQueue("test.queue", "payload"));

        verify(jmsTemplate, times(1)).convertAndSend("test.queue", "payload");
    }
}

