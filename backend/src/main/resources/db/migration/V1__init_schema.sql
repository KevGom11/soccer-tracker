-- USERS
CREATE TABLE app_user (
                          id         BIGSERIAL PRIMARY KEY,
                          email      VARCHAR(255) UNIQUE NOT NULL,
                          name       VARCHAR(120),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- TEAMS
CREATE TABLE team (
                      id           BIGSERIAL PRIMARY KEY,
                      name         VARCHAR(120) NOT NULL,
                      short_name   VARCHAR(120),
                      tla          VARCHAR(3),
                      area         VARCHAR(120),
                      league       VARCHAR(80),
                      country      VARCHAR(80),
                      external_ref VARCHAR(120),
                      created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                      CONSTRAINT uq_team_name_league UNIQUE (name, league)
);

-- MATCHES (kickoff_at ↔ ZonedDateTime)
CREATE TABLE match (
                       id            BIGSERIAL PRIMARY KEY,
                       home_team_id  BIGINT NOT NULL REFERENCES team(id) ON DELETE RESTRICT,
                       away_team_id  BIGINT NOT NULL REFERENCES team(id) ON DELETE RESTRICT,
                       kickoff_at    TIMESTAMPTZ NOT NULL,
                       venue         VARCHAR(160),
                       status        VARCHAR(40) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED|LIVE|FT
                       home_score    INTEGER,
                       away_score    INTEGER,
                       external_id   BIGINT,               -- used for dedupe
                       competition   VARCHAR(160),
                       external_ref  VARCHAR(120),
                       created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_match_kickoff     ON match(kickoff_at);
CREATE INDEX idx_match_teams       ON match(home_team_id, away_team_id);
CREATE INDEX idx_match_external_id ON match(external_id);

-- SUBSCRIPTIONS (with reminder settings IN this table)
CREATE TABLE subscription (
                              id            BIGSERIAL PRIMARY KEY,
                              user_id       BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
                              team_id       BIGINT NOT NULL REFERENCES team(id)     ON DELETE CASCADE,
                              hours_before  INTEGER NOT NULL DEFAULT 2,
                              active        BOOLEAN NOT NULL DEFAULT TRUE,
                              last_sent_at  TIMESTAMPTZ,
                              created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                              CONSTRAINT uq_subscription UNIQUE (user_id, team_id)
);

CREATE INDEX idx_subscription_user   ON subscription(user_id);
CREATE INDEX idx_subscription_team   ON subscription(team_id);
CREATE INDEX idx_subscription_active ON subscription(active);
