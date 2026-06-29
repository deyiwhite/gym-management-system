-- Enforce class registration status and rating consistency.
-- Run once after class_record exists. Back up the database before running.

UPDATE class_record
SET status = 1,
    completed_sessions = total_sessions
WHERE rating IS NOT NULL
  AND status <> 1;

ALTER TABLE class_record
    ADD CONSTRAINT chk_class_record_status
        CHECK (status IN (0, 1, 2)),
    ADD CONSTRAINT chk_class_record_rating_range
        CHECK (rating IS NULL OR rating BETWEEN 0 AND 10),
    ADD CONSTRAINT chk_class_record_rating_completed
        CHECK (rating IS NULL OR status = 1),
    ADD CONSTRAINT chk_class_record_sessions
        CHECK (
            total_sessions >= 1
            AND completed_sessions >= 0
            AND completed_sessions <= total_sessions
            AND (status <> 1 OR completed_sessions = total_sessions)
        );
