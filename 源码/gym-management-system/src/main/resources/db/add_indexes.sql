-- Standalone index script.
-- gym_management_system.sql already contains these indexes; run this file only
-- when you imported an older schema without the index section.

ALTER TABLE class_record ADD INDEX idx_cr_member_id (member_id);
ALTER TABLE class_record ADD INDEX idx_cr_class_id (class_id);
ALTER TABLE class_record ADD INDEX idx_cr_rating (rating);
ALTER TABLE class_record ADD INDEX idx_cr_class_rating (class_id, rating);
ALTER TABLE class_table ADD INDEX idx_class_coach_id (coach_id);
ALTER TABLE class_equipment ADD INDEX idx_ce_equipment_id (equipment_id);
ALTER TABLE member ADD INDEX idx_member_name (member_name);
ALTER TABLE employee ADD INDEX idx_employee_position (position);
ALTER TABLE equipment ADD INDEX idx_equipment_name (equipment_name);
