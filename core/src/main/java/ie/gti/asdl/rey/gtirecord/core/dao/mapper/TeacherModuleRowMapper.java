package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */public class TeacherModuleRowMapper implements RowMapper<TeacherModule> {

    private static final TeacherRowMapper teacherRowMapper = new TeacherRowMapper();
    private static final ModuleRowMapper moduleRowMapper = new ModuleRowMapper();

    @NotNull
    @Override
    public TeacherModule mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        TeacherModule teacherModule = new TeacherModule();
        teacherModule.setTeacher(teacherRowMapper.mapRow(resultSet, rowNum));
        teacherModule.setModule(moduleRowMapper.mapRow(resultSet, rowNum));

        return teacherModule;
    }

}