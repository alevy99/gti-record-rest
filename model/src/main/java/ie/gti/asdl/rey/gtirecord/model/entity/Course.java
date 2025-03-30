package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Course {

    private int id;

    private String name;
    
    private String code;
    
    private CourseType courseType;

    private Department department;

    private QQILevel qqiLevel;

}
