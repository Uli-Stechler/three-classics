package com.threeclassics.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoveRepository extends JpaRepository<MoveEntity, Long> {

    List<MoveEntity> findByGameIdOrderByPlyAsc(UUID gameId);

    long countByGameId(UUID gameId);
}
