package com.gym.controller;

import com.gym.pojo.ClassRecordVO;
import com.gym.pojo.ClassTable;
import com.gym.pojo.Equipment;
import com.gym.service.ClassRecordService;
import com.gym.service.ClassTableService;
import com.gym.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/class")
public class ApiClassController {

    @Autowired
    private ClassTableService classTableService;
    @Autowired
    private ClassRecordService classRecordService;
    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/selClass")
    public Map<String, Object> selectClass() {
        List<ClassTable> classList = classTableService.findAll();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classList", classList);
        return resp;
    }

    @GetMapping("/selClassRecord")
    public Map<String, Object> selectClassRecord(Integer classId) {
        List<ClassRecordVO> recordList = classRecordService.selectByClassId(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classRecordList", recordList);
        return resp;
    }

    @GetMapping("/record/search")
    public Map<String, Object> searchClassRecord(Integer classId, String memberKeyword, Integer status,
                                                 String ratingStatus, Double lowRating, Double highRating) {
        List<ClassRecordVO> recordList = classRecordService.searchRecords(
                classId, memberKeyword, status, ratingStatus, lowRating, highRating
        );
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("classRecordList", recordList);
        return resp;
    }

    @GetMapping("/analysis")
    public Map<String, Object> analysis(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("summary", classTableService.selectAnalysisSummary());
        resp.put("courseAnalysisRows", classTableService.selectCourseAnalysisRows(page, pageSize));
        resp.put("total", classTableService.countCourseAnalysisRows());
        return resp;
    }

    @GetMapping("/toAddClass")
    public Map<String, Object> toAddClass() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/addClass")
    public ResponseEntity<Map<String, Object>> addClass(ClassTable classTable) {
        classTableService.insertClass(classTable);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/classDetail")
    public Map<String, Object> classDetail(Integer classId) {
        ClassTable cls = classTableService.selectByClassId(classId);
        List<Equipment> equipList = equipmentService.selectByClassId(classId);
        int enrolledCount = classRecordService.countByClassId(classId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", cls != null);
        resp.put("course", cls);
        resp.put("equipmentList", equipList);
        resp.put("enrolledCount", enrolledCount);
        return resp;
    }

    @Transactional
    @PostMapping("/delClass")
    public ResponseEntity<Map<String, Object>> deleteClass(Integer classId) {
        classTableService.deleteClassByClassId(classId);
        classTableService.deleteRecordByClassId(classId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}
