package com.example.outnowbackend.config;

import com.example.outnowbackend.event.domain.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HibernateSearchIndexer {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeHibernateSearch() {
        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer(Event.class)
                    .threadsToLoadObjects(4)
                    .startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Lucene indexing was interrupted: " + e.getMessage());
        }
    }
}
