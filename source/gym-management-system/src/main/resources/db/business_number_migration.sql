-- Run once when upgrading an existing gym_db.
-- Keep numeric primary keys as internal IDs, and add formatted business numbers
-- for member cards and employee accounts.

ALTER TABLE member
    ADD COLUMN member_no VARCHAR(20) NULL COMMENT '会员卡号' AFTER member_id;

ALTER TABLE employee
    ADD COLUMN employee_no VARCHAR(20) NULL COMMENT '员工工号' AFTER employee_id;

UPDATE member m
JOIN (
    SELECT member_id,
           CONCAT('2026', LPAD(ROW_NUMBER() OVER (ORDER BY member_id), 4, '0')) AS new_no
    FROM member
) x ON m.member_id = x.member_id
SET m.member_no = x.new_no;

UPDATE employee e
JOIN (
    SELECT employee_id,
           LPAD(ROW_NUMBER() OVER (ORDER BY employee_id), 3, '0') AS new_no
    FROM employee
) x ON e.employee_id = x.employee_id
SET e.employee_no = x.new_no;

ALTER TABLE member
    MODIFY member_no VARCHAR(20) NOT NULL COMMENT '会员卡号',
    ADD UNIQUE KEY uk_member_no (member_no);

ALTER TABLE employee
    MODIFY employee_no VARCHAR(20) NOT NULL COMMENT '员工工号',
    ADD UNIQUE KEY uk_employee_no (employee_no);

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
