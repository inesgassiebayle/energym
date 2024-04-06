package org.austral.ing.lab1.dto;

public class RoomDeletionDto {
    private String name;
    public RoomDeletionDto(String name){
        this.name = name;
    }
    public RoomDeletionDto(){
    }

    public String getName(){
        return name;
    }
}
