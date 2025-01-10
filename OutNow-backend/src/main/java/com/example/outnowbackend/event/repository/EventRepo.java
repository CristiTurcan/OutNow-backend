package com.example.outnowbackend.event.repository;

import com.example.outnowbackend.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Integer>{
}
