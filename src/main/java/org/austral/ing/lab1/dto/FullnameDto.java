package org.austral.ing.lab1.dto;

public class FullnameDto {
    private String firstName;
    private String lastName;

    public FullnameDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
