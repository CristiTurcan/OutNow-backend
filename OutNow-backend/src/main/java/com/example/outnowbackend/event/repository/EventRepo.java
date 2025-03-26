package com.example.outnowbackend.event.repository;

import com.example.outnowbackend.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepo extends JpaRepository<Event, Integer>{
    List<Event> findByBusinessAccount_Id(Integer id);

}
