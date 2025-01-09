package com.example.outnowbackend.repository;

import com.example.outnowbackend.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Integer>{
}
