-- Indexes for paged class registration queries.
-- Run once after class_record exists.

CREATE INDEX idx_class_record_join_time_record
    ON class_record (join_time DESC, record_id DESC);

CREATE INDEX idx_class_record_class_time_record
    ON class_record (class_id, join_time DESC, record_id DESC);

CREATE INDEX idx_class_record_status_time_record
    ON class_record (status, join_time DESC, record_id DESC);

CREATE INDEX idx_class_record_rating_time_record
    ON class_record (rating, join_time DESC, record_id DESC);
