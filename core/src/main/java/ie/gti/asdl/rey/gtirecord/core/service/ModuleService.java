package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ModuleService {

    Optional<Module> getById(int id);

    List<Module> getAll();

    Optional<Integer> insert(Module module);

    void update(Module module);

    void delete(int id);

}
