ALTER TABLE users ADD COLUMN approved_by VARCHAR(50);
ALTER TABLE users ADD COLUMN approval_timestamp TIMESTAMP;
ALTER TABLE users ALTER COLUMN account_status SET DEFAULT 'PENDING';

CREATE TABLE IF NOT EXISTS user_permissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission VARCHAR(50) NOT NULL,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by VARCHAR(50),
    UNIQUE(user_id, permission)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(50) NOT NULL,
    target_user VARCHAR(50),
    target_id VARCHAR(100),
    details VARCHAR(1000),
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE bills ADD COLUMN archived BOOLEAN DEFAULT FALSE;
ALTER TABLE bills ADD COLUMN archived_at TIMESTAMP;
ALTER TABLE bills ADD COLUMN archived_by VARCHAR(50);
ALTER TABLE bills ADD COLUMN deleted_by VARCHAR(50);
ALTER TABLE bills ADD COLUMN deleted_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_bills_archived ON bills(archived);
CREATE INDEX IF NOT EXISTS idx_audit_logs_performed_at ON audit_logs(performed_at);
CREATE INDEX IF NOT EXISTS idx_user_permissions_user_id ON user_permissions(user_id);
