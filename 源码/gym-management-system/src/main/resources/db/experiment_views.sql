-- Standalone view script for experiment 4.
-- gym_management_system.sql already contains this view; run this file only
-- when you imported an older schema without the view section.

CREATE OR REPLACE VIEW v_member_course_rating AS
SELECT m.member_no,
       m.member_name,
       m.gender,
       ct.class_id,
       ct.class_name,
       cr.record_id,
       cr.join_time,
       cr.status,
       cr.rating
FROM member m
JOIN class_record cr ON m.member_id = cr.member_id
JOIN class_table ct ON cr.class_id = ct.class_id
WHERE cr.status = 1
  AND cr.rating IS NOT NULL;
