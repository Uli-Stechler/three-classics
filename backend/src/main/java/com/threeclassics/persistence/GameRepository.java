package com.threeclassics.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {

    /** Wins, losses and draws of finished games against the computer, per type and difficulty. */
    @Query(value = """
            select type, difficulty,
                   count(*) filter (where winner = human_side)                       as wins,
                   count(*) filter (where winner is not null and winner <> human_side) as losses,
                   count(*) filter (where winner is null)                            as draws
            from games
            where status = 'FINISHED' and mode = 'VS_AI'
            group by type, difficulty
            """, nativeQuery = true)
    List<StatsRow> stats();

    interface StatsRow {
        String getType();

        String getDifficulty();

        long getWins();

        long getLosses();

        long getDraws();
    }
}
