package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Tiquete;

@Repository
public interface TiqueteRepository extends MongoRepository<Tiquete, String> {
}