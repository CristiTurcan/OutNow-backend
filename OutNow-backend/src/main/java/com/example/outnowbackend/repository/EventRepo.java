package com.example.outnowbackend.repository;

import com.example.outnowbackend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EventRepo extends JpaRepository<Event, Integer>{
}
