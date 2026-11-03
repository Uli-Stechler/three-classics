create table games (
    id            uuid primary key,
    type          varchar(32) not null,
    mode          varchar(16) not null,
    difficulty    varchar(16) not null,
    human_side    varchar(8)  not null,
    status        varchar(16) not null,
    winner        varchar(8),
    result_reason text,
    points        int,
    state         jsonb       not null,
    version       bigint      not null default 0,
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now()
);

-- Stats are computed from finished games against the computer.
create index games_finished_vs_ai_idx on games (type, difficulty)
    where status = 'FINISHED' and mode = 'VS_AI';

create table moves (
    id         bigserial primary key,
    game_id    uuid        not null references games (id) on delete cascade,
    ply        int         not null,
    actor      varchar(8)  not null,
    move       jsonb       not null,
    created_at timestamptz not null default now(),
    unique (game_id, ply)
);
