package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.CourseRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.GroupModuleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.GroupRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.ModuleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.validation.OnCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * @author Andrei Levchenko
 */
@Repository
public class GroupModuleDaoImpl implements GroupModuleDao {

    private static final GroupModuleRowMapper groupModuleRowMapper = new GroupModuleRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final ValidationService validationService;

    @Autowired
    public GroupModuleDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
    }

    @Override
    public List<GroupModule> getAll() {
        final String sql = """
                SELECT gm.id as group_module_id,
                       m.id as module_id, m.name as module_name, m.code as module_code,
                       g.id as group_id, g.name as group_name, g.code as group_code,
                       p.first_name, p.last_name
                FROM group_has_module gm
                INNER JOIN module m ON m.id = gm.module_id
                INNER JOIN `group` g ON g.id = gm.group_id
                LEFT OUTER JOIN teacher t ON t.person_id = gm.teacher_person_id
                INNER JOIN person p ON p.id = t.person_id
            """;
        return jdbcTemplate.query(sql, groupModuleRowMapper);
    }

    @Override
    public List<GroupModule> getByGroupId(Integer groupId) {
        if (groupId == null) return new ArrayList<>();
        final String sql = """
                SELECT gm.id as group_module_id,
                       m.id as module_id, m.name as module_name, m.code as module_code,
                       g.id as group_id, g.name as group_name, g.code as group_code,
                       p.first_name, p.last_name
                FROM group_has_module gm
                INNER JOIN module m ON m.id = gm.module_id
                INNER JOIN `group` g ON g.id = gm.group_id
                LEFT OUTER JOIN teacher t ON t.person_id = gm.teacher_person_id
                INNER JOIN person p ON p.id = t.person_id
                WHERE gm.group_id = ?
            """;
        return jdbcTemplate.query(sql, groupModuleRowMapper, groupId);
    }

//    @Override
//    public Map<Group, List<Module>> getAllGroupedByGroup() {
//        final String sql = """
//                    SELECT c.id as course_id, c.department_id, c.course_type_id, c.qqi_level_id, c.name as course_name,
//                           c.code as course_code, d.name as department_name, ct.type as course_type_name, q.name as qqi_name
//                    FROM module m, `group` g, group_has_module ct, qqi_level q
//                    WHERE c.department_id = d.id and c.course_type_id = ct.id and c.qqi_level_id = q.id
//                    ORDER BY c.department_id;
//                """;
//        return jdbcTemplate.query(sql, rs -> {
//                    Map<Group, List<ie.gti.asdl.rey.gtirecord.model.entity.Module>> map = new HashMap<>();
//                    int rowNum = 0;
//                    while (rs.next()) {
//                        Module module = moduleRowMapper.mapRow(rs, rowNum);
//                        assert module != null;
//                        map.computeIfAbsent(module.getGroup(), department -> new ArrayList<>()).add(module);
//                        rowNum++;
//                    }
//                    return map;
//                }
//        );
//    }

    @Override
    public Optional<Integer> insert(GroupModule groupModule) {
        Optional<Integer> newGroupModuleId  = insert(groupModule.getGroup().getId(), groupModule.getModule().getId(),
                groupModule.getTeacher().getPerson().getId());
        newGroupModuleId.ifPresent(groupModule::setId);
        return newGroupModuleId;
    }

    @Override
    public Optional<Integer> insert(Integer groupId, Integer moduleId, Integer teacherPersonId) {
        if ((groupId == null) || (moduleId == null)) return Optional.empty();
        final String sql = "INSERT INTO group_has_module (group_id, module_id, teacher_person_id) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, groupId);
                ps.setInt(2, moduleId);
                if (teacherPersonId != null) {
                    ps.setInt(3, teacherPersonId);
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            return Optional.of(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void update(Integer groupId, Integer moduleId, Integer teacherPersonId) {
        if ((groupId == null) || (moduleId == null)) return;
        final String sql = "UPDATE group_has_module SET teacher_person_id = ? WHERE group_id = ? and module_id = ?";
        jdbcTemplate.update(sql, teacherPersonId, groupId, moduleId);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) return;
        final String sql = "DELETE FROM group_has_module WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(Integer groupId, Integer moduleId) {
        if ((groupId == null) || (moduleId == null)) return;
        final String sql = "DELETE FROM group_has_module WHERE group_id = ? and module_id = ?";
        jdbcTemplate.update(sql, groupId, moduleId);
    }
}
