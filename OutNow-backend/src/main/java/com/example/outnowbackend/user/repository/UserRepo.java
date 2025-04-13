package com.example.outnowbackend.user.repository;

import com.example.outnowbackend.event.domain.Event;
import com.example.outnowbackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Modifying
    @Query(value = "INSERT INTO user_favorited_events (user_id, event_id) VALUES (:userId, :eventId)", nativeQuery = true)
    void addFavoriteEventNative(@Param("userId") Integer userId, @Param("eventId") Integer eventId);

    @Modifying
    @Query(value = "DELETE FROM user_favorited_events WHERE user_id = :userId AND event_id = :eventId", nativeQuery = true)
    void removeFavoriteEventNative(@Param("userId") Integer userId, @Param("eventId") Integer eventId);

    @Query(value = "SELECT e.* FROM event e " +
            "INNER JOIN user_favorited_events ufe ON e.event_id = ufe.event_id " +
            "WHERE ufe.user_id = :userId",
            nativeQuery = true)
    List<Event> findFavoritedEventsByUserId(@Param("userId") Integer userId);

}
