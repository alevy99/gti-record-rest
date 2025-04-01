package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class GroupModule {

    private Integer id;

    private Group group;

    private Module module;

    private Teacher teacher;
}
