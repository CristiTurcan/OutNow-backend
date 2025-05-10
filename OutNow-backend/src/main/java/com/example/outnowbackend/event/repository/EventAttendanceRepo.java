package com.example.outnowbackend.event.repository;

import com.example.outnowbackend.event.domain.EventAttendance;
import com.example.outnowbackend.event.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventAttendanceRepo extends JpaRepository<EventAttendance, Integer> {
    long countByEvent(Event event);

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.event.eventId = :eventId")
    long countByEventId(@Param("eventId") Integer eventId);

    int deleteByUser_UseridAndEvent_EventId(Integer userid, Integer eventId);
}
