package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Tiquete;

@Repository
public interface TiqueteRepository extends JpaRepository<Tiquete, Integer> {
}