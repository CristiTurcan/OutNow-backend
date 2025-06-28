package com.example.outnowbackend.user.repository;

import com.example.outnowbackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    boolean existsByUsername(String username);


    @Query("SELECT COUNT(u) FROM User u JOIN u.favoritedEvents e WHERE e.eventId = :eventId")
    long countByFavoritedEvents_EventId(@Param("eventId") Integer eventId);

    @Query("SELECT COUNT(u) FROM User u JOIN u.goingEvents e WHERE e.eventId = :eventId")
    long countAttendeesByEventId(@Param("eventId") Integer eventId);

    @Query("SELECT u.userid FROM User u JOIN u.goingEvents e WHERE e.eventId = :eventId")
    List<Integer> findAttendeeIdsByEventId(@Param("eventId") Integer eventId);

    @Modifying
    @Query(value = "DELETE FROM user_going_events WHERE event_id = :eventId", nativeQuery = true)
    void deleteGoingByEventId(@Param("eventId") Integer eventId);

}
