package com.genesis.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesis.common.domain.CommonModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
@Slf4j
public class Archiver {

    private final ObjectMapper jsonMapper = new ObjectMapper();


    private final CommonRepository commonRepository;

    public Archiver(CommonRepository commonRepository) {
        this.commonRepository = commonRepository;
    }

    @Bean
    public Consumer<Message<byte[]>> store() {
        return (message) -> {

            CommonModel value;
            try {
                value = jsonMapper.readValue(new String(message.getPayload(), StandardCharsets.UTF_8), CommonModel.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Received {}", value);

            Common common = Common.builder()
                    .name(value.getName())
                    .age(value.getAge())
                    .build();

            commonRepository.save(common);

            log.info("Stored successfully!");
        };
    }
}
