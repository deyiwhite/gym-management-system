-- Course operations demo data.
-- Scope: only rewrites class registration records.
-- Safety: keeps member, employee, equipment, admin, and class definitions intact.

START TRANSACTION;

DROP TEMPORARY TABLE IF EXISTS tmp_course_member_rank;
CREATE TEMPORARY TABLE tmp_course_member_rank AS
SELECT member_id, ROW_NUMBER() OVER (ORDER BY member_id) AS rn
FROM member;

DELETE FROM class_record;

INSERT INTO class_record (
    member_id,
    class_id,
    join_time,
    status,
    total_sessions,
    completed_sessions,
    rating
)
SELECT
    ranked_members.member_id,
    targets.class_id,
    TIMESTAMP(
        DATE_SUB('2026-06-13', INTERVAL ((ranked_members.rn + targets.class_id) % 35) DAY),
        MAKETIME(8 + ((ranked_members.rn + targets.class_id) % 11), (ranked_members.rn * 7) % 60, 0)
    ) AS join_time,
    CASE
        WHEN ranked_members.rn <= targets.target_count - targets.unrated_count THEN 1
        WHEN ranked_members.rn % 13 = 0 THEN 2
        WHEN ranked_members.rn % 4 = 0 THEN 1
        ELSE 0
    END AS status,
    CASE
        WHEN targets.class_id IN (2, 3, 5, 7, 8, 14) THEN 8
        WHEN targets.class_id IN (1, 6, 10, 11, 12) THEN 6
        ELSE 4
    END AS total_sessions,
    CASE
        WHEN ranked_members.rn <= targets.target_count - targets.unrated_count THEN
            CASE
                WHEN targets.class_id IN (2, 3, 5, 7, 8, 14) THEN 8
                WHEN targets.class_id IN (1, 6, 10, 11, 12) THEN 6
                ELSE 4
            END
        WHEN ranked_members.rn % 13 = 0 THEN 0
        WHEN ranked_members.rn % 4 = 0 THEN
            CASE
                WHEN targets.class_id IN (2, 3, 5, 7, 8, 14) THEN 8
                WHEN targets.class_id IN (1, 6, 10, 11, 12) THEN 6
                ELSE 4
            END
        ELSE ranked_members.rn % 3
    END AS completed_sessions,
    CASE
        WHEN ranked_members.rn > targets.target_count - targets.unrated_count THEN NULL
        ELSE ROUND(
            LEAST(
                9.8,
                GREATEST(
                    4.5,
                    targets.rating_base +
                    CASE ranked_members.rn % 7
                        WHEN 0 THEN -0.4
                        WHEN 1 THEN 0.2
                        WHEN 2 THEN -0.1
                        WHEN 3 THEN 0.4
                        WHEN 4 THEN 0.0
                        WHEN 5 THEN -0.2
                        ELSE 0.1
                    END
                )
            ),
            1
        )
    END AS rating
FROM (
    SELECT member_id, rn
    FROM tmp_course_member_rank
) ranked_members
JOIN (
    SELECT 2 AS class_id, 42 AS target_count, 8 AS unrated_count, 8.5 AS rating_base
    UNION ALL SELECT 3, 36, 9, 7.8
    UNION ALL SELECT 10, 31, 6, 8.2
    UNION ALL SELECT 8, 26, 4, 8.0
    UNION ALL SELECT 5, 22, 3, 8.1
    UNION ALL SELECT 1, 18, 2, 8.7
    UNION ALL SELECT 7, 14, 2, 9.0
    UNION ALL SELECT 11, 10, 1, 8.3
    UNION ALL SELECT 9, 7, 2, 7.4
    UNION ALL SELECT 6, 5, 1, 7.0
    UNION ALL SELECT 4, 3, 1, 7.3
) targets ON ranked_members.rn <= targets.target_count;

DROP TEMPORARY TABLE IF EXISTS tmp_course_member_rank;

COMMIT;
