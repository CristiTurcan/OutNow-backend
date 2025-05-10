package com.example.outnowbackend.event.repository;

import com.example.outnowbackend.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EventRepo extends JpaRepository<Event, Integer> {
    List<Event> findByBusinessAccount_Id(Integer id);

    // find events starting at exactly this date & time
    @Query("SELECT e FROM Event e WHERE e.eventDate = :date AND e.eventTime = :time")
    List<Event> findByEventDateAndEventTime(@Param("date") LocalDate date,
                                            @Param("time") LocalTime time);

    @Query("""
      SELECT e 
        FROM Event e 
       WHERE e.eventDate > :today 
          OR (e.eventDate = :today AND e.eventTime >= :now)
      """)
    List<Event> findUpcoming(
            @Param("today") LocalDate today,
            @Param("now")   LocalTime now
    );
}
