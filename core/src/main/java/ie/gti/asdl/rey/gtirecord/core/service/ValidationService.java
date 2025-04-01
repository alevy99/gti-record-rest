package ie.gti.asdl.rey.gtirecord.core.service;

import org.springframework.stereotype.Service;

/**
 * @author Andrei Levchenko
 */
@Service
public interface ValidationService {

    boolean isEmailValid(String email);

}
