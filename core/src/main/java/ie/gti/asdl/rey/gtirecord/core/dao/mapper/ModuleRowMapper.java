package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class ModuleRowMapper implements RowMapper<Module> {

    @NotNull
    @Override
    public Module mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Module module = new Module();
        helper.setIntIfPresent("module_id", module::setId);
        helper.setStringIfPresent("module_name", module::setName);
        helper.setStringIfPresent("module_code", module::setCode);

        return module;
    }
}