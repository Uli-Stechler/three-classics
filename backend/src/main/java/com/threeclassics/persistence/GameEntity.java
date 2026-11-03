package com.threeclassics.persistence;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Side;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameMode mode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    /** The human's seat in VS_AI games; P1 for LOCAL games. */
    @Enumerated(EnumType.STRING)
    @Column(name = "human_side", nullable = false)
    private Side humanSide;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private Side winner;

    @Column(name = "result_reason")
    private String resultReason;

    private Integer points;

    /** The engine state as JSON. */
    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String state;

    /** Optimistic lock: every request names the version it was based on. Null until first saved. */
    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected GameEntity() {
    }

    public GameEntity(GameType type, GameMode mode, Difficulty difficulty, Side humanSide, String state) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.mode = mode;
        this.difficulty = difficulty;
        this.humanSide = humanSide;
        this.status = GameStatus.IN_PROGRESS;
        this.state = state;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void finish(Side winner, String reason, int points) {
        this.status = GameStatus.FINISHED;
        this.winner = winner;
        this.resultReason = reason;
        this.points = points;
    }

    public UUID getId() {
        return id;
    }

    public GameType getType() {
        return type;
    }

    public GameMode getMode() {
        return mode;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Side getHumanSide() {
        return humanSide;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Side getWinner() {
        return winner;
    }

    public String getResultReason() {
        return resultReason;
    }

    public Integer getPoints() {
        return points;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getVersion() {
        return version == null ? 0 : version;
    }
}
