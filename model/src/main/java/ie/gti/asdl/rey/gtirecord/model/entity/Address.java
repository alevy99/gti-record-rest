package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Address implements Serializable {

    private Integer personId;

    private String line1;

    private String line2;

    private String city;

    private String county;

    private String country;

    private String eirCode;

}
