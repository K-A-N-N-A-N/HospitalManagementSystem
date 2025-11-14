// src/main/java/com/hospitalmanagement/hospital_crud/config/ActiveMQConfig.java
package com.hospitalmanagement.hospital_crud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class ActiveMQConfig {

    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        // register JavaTimeModule so LocalDateTime serializes properly
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        converter.setTargetType(MessageType.TEXT); // send JSON text messages
        converter.setTypeIdPropertyName("_type");  // optional type id header
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory,
                                   MappingJackson2MessageConverter converter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false); // queue
        jmsTemplate.setDeliveryPersistent(true);
        jmsTemplate.setSessionTransacted(true);

        // Use JSON converter (not null)
        jmsTemplate.setMessageConverter(converter);
        return jmsTemplate;
    }
}
