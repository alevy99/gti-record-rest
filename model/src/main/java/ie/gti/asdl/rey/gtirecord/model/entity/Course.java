package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class Course {
    
    private String name;
    
    private String code;
    
    private boolean isFullTime;

    private CourseType courseType;

    private Department department;

    private QQILevel qqiLevel;

}
