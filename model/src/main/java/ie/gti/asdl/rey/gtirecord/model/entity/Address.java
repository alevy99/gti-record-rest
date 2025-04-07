package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class Address implements Serializable {

    @KeyField
    @NotNull
    private Integer personId;

    private String line1;

    private String line2;

    @ShortDescriptionField
    private String city;

    private String county;

    private String country;

    private String eirCode;

}
