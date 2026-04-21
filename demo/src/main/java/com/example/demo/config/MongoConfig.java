package com.example.demo.config;

import java.util.Arrays;

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

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
            new RolReadingConverter(),
            new RolWritingConverter(),
            new LocalTimeReadConverter(),
            new LocalTimeWriteConverter()
        ));
    }
}