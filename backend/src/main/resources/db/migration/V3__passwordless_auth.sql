-- One-time 6-digit codes for passwordless login
CREATE TABLE IF NOT EXISTS login_token (
                                           id          BIGSERIAL PRIMARY KEY,
                                           email       VARCHAR(255) NOT NULL,
    code_hash   VARCHAR(255) NOT NULL,      -- store a hash of the 6-digit code
    expires_at  TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
CREATE INDEX IF NOT EXISTS idx_login_token_email ON login_token(email);

-- Sessions (simple UUID tokens)
CREATE TABLE IF NOT EXISTS session_token (
                                             token       UUID PRIMARY KEY,
                                             user_id     BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at  TIMESTAMPTZ
    );
CREATE INDEX IF NOT EXISTS idx_session_token_user ON session_token(user_id);
