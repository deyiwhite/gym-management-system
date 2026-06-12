package com.gym.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class ClassRecordVO {
    private Integer recordId;
    private Integer memberId;
    private String memberName;
    private Integer classId;
    private String className;
    private String coachName;
    private Date classBegin;
    private Date joinTime;
    private Integer status;
    private Integer totalSessions;
    private Integer completedSessions;
    private Double rating;
}
