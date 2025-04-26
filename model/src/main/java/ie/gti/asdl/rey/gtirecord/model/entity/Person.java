package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionFormat;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Person implements Serializable {

    @KeyField
    @EqualsAndHashCode.Include
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField(format = ShortDescriptionFormat.NAME_FORMAT)
    @NotBlank(message = "Person's first name must be provided")
    private String firstName;

    @ShortDescriptionField(order = 1, format = ShortDescriptionFormat.NAME_FORMAT)
    @NotBlank(message = "Person's last name must be provided")
    private String lastName;

    private String gender;

    private LocalDate dateOfBirth;

    private String ppsn;

    private String email;

    private String phoneNum;

    private Address address;

}
