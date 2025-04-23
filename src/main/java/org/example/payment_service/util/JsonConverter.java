package org.example.payment_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.payment_service.model.dto.CreatePaymentTransactionRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonConverter {
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T toObject(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Json deserializing exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Json serializing exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Object fromJson(String message, Class<CreatePaymentTransactionRequest> createPaymentTransactionRequestClass) {
    }
}
