package com.gym.pojo;

import lombok.Data;

@Data
public class ClassTable {
    private Integer classId;
    private String className;
    private String classBegin;
    private String classTime;
    private String coach;
    private Integer coachId;
    private Integer classLevel;
    private Integer classDuration;
    private Integer maxCapacity;
    private String classDescription;
    private Double price;
}
