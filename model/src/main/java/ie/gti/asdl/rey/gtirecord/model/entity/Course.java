package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Course {

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String name;
    
    private String code;
    
    private CourseType courseType;

    private Department department;

    private QQILevel qqiLevel;

}
