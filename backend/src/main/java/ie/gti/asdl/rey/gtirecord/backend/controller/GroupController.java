package ie.gti.asdl.rey.gtirecord.backend.controller;

import ie.gti.asdl.rey.gtirecord.core.service.GroupService;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/modules")
    public List<Group> getGroups() {
        return groupService.getAll();
    }

}
