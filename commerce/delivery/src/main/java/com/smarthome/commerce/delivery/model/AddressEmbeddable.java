package com.smarthome.commerce.delivery.model;

import com.smarthome.commerce.api.warehouse.AddressDto;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;

    protected AddressEmbeddable() {
    }

    public AddressEmbeddable(AddressDto address) {
        this.country = address.country();
        this.city = address.city();
        this.street = address.street();
        this.house = address.house();
        this.flat = address.flat();
    }

    public String getCountry() {
        return country;
    }

    public String getStreet() {
        return street;
    }

    public AddressDto toDto() {
        return new AddressDto(country, city, street, house, flat);
    }
}
