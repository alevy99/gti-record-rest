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
 * REST controller exposing endpoints for retrieving {@link Group} information.
 * <p>
 * This controller is only active when the {@code "web"} Spring profile is
 * enabled, and delegates all data access to {@link GroupService}.
 *
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class GroupController {

    /** Service used to retrieve group data. */
    private final GroupService groupService;

    /**
     * Creates a new {@code GroupController} with the given group service.
     *
     * @param groupService the service used to look up groups
     */
    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Retrieves all groups.
     *
     * @return a list of all {@link Group} records
     */
    @GetMapping("/groups")
    public List<Group> getGroups() {
        return groupService.getAll();
    }

}
