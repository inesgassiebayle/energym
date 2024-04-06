package org.austral.ing.lab1.dto;

import com.google.gson.JsonObject;

public class RoomCreationDto {
    private String name;
    private String capacity;
    private String activities;

    public RoomCreationDto(String name, String capacity, String activities){
        this.name = name;
        this.capacity = capacity;
        this.activities = activities;
    }

    public RoomCreationDto(){}

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
