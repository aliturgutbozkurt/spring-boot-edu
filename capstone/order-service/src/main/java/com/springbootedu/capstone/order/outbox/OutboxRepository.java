package com.springbootedu.capstone.order.outbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    /** The next unpublished events, locked; rows another relay instance has locked are skipped, not waited for. */
    @NativeQuery("""
            SELECT * FROM outbox
            WHERE published_at IS NULL
            ORDER BY id
            LIMIT 100
            FOR UPDATE SKIP LOCKED""")
    List<OutboxEvent> lockNextToPublish();
}
