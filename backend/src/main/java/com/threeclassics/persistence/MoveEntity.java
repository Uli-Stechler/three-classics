package com.threeclassics.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "moves")
public class MoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(nullable = false)
    private int ply;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Actor actor;

    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String move;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MoveEntity() {
    }

    public MoveEntity(UUID gameId, int ply, Actor actor, String move) {
        this.gameId = gameId;
        this.ply = ply;
        this.actor = actor;
        this.move = move;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public int getPly() {
        return ply;
    }

    public Actor getActor() {
        return actor;
    }

    public String getMove() {
        return move;
    }
}
