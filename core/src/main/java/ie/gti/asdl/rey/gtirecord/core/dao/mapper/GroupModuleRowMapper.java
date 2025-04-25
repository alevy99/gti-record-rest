package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class GroupModuleRowMapper implements RowMapper<GroupModule> {

    private static final GroupRowMapper groupRowMapper = new GroupRowMapper();
    private static final ModuleRowMapper moduleRowMapper = new ModuleRowMapper();
    private static final TeacherRowMapper teacherRowMapper = new TeacherRowMapper();

    @NotNull
    @Override
    public GroupModule mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        GroupModule groupModule = InstanceFactory.create(GroupModule.class);
        helper.setIntIfPresent("group_module_id", groupModule::setId);

        groupModule.setModule(moduleRowMapper.mapRow(rs, rowNum));
        groupModule.setGroup(groupRowMapper.mapRow(rs, rowNum));
        groupModule.setTeacher(teacherRowMapper.mapRow(rs, rowNum));

        return groupModule;
    }
}