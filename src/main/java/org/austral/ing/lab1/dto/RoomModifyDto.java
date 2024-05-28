package org.austral.ing.lab1.dto;

public class RoomModifyDto {
    private String name;
    private String newName;
    private String capacity;
    private String activities;

    public RoomModifyDto(String name,String newName, String capacity, String activities){
        this.name = name;
        this.newName = newName;
        this.capacity = capacity;
        this.activities = activities;
    }

    public RoomModifyDto(){}

    public String getName(){
        return name;
    }
    public String getNewName(){return newName;}
    public Integer getCapacity(){
        return Integer.parseInt(capacity);
    }
    public String[] getActivities(){
        String[] activities1 =  activities.split(",");
        if (activities1.length == 1) {
            return new String[0];
        }
        return activities1;
    }
}
