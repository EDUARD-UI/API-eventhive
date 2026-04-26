package com.example.demo.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.demo.converter.LocalTimeReadConverter;
import com.example.demo.converter.LocalTimeWriteConverter;
import com.example.demo.converter.RolReadingConverter;
import com.example.demo.converter.RolWritingConverter;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.demo.repository")
@EnableMongoAuditing
public class MongoConfig {

    @Autowired(required = false)
    private RolReadingConverter rolReadingConverter;

    @Autowired(required = false)
    private RolWritingConverter rolWritingConverter;

    @Autowired(required = false)
    private LocalTimeReadConverter localTimeReadConverter;

    @Autowired(required = false)
    private LocalTimeWriteConverter localTimeWriteConverter;

    @Bean
    public MongoCustomConversions customConversions() {
        // Inicializar conversores si no están inyectados
        RolReadingConverter rolRead = rolReadingConverter != null ? 
            rolReadingConverter : new RolReadingConverter();
        RolWritingConverter rolWrite = rolWritingConverter != null ? 
            rolWritingConverter : new RolWritingConverter();
        LocalTimeReadConverter localRead = localTimeReadConverter != null ? 
            localTimeReadConverter : new LocalTimeReadConverter();
        LocalTimeWriteConverter localWrite = localTimeWriteConverter != null ? 
            localTimeWriteConverter : new LocalTimeWriteConverter();

        return new MongoCustomConversions(Arrays.asList(
            rolRead,
            rolWrite,
            localRead,
            localWrite
        ));
    }
}
