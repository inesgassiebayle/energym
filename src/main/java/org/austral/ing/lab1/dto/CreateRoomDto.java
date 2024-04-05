package org.austral.ing.lab1.dto;

public class CreateRoomDto {
    private String name;
    private String capacity;
    private String activities;

    public CreateRoomDto(String name, String capacity, String activities){
        this.capacity=capacity;
        this.name=name;
        this.activities=activities;
    }

    public String getName(){
        return name;
    }

    public Integer getCapacity(){
        return Integer.parseInt(capacity);
    }

    public String[] getActivities(){
        return activities.split(",");
    }




}
