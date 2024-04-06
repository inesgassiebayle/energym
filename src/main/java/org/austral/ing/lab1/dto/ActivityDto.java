package org.austral.ing.lab1.dto;

public class ActivityDto {
    private String name;
    public ActivityDto(String name){
        this.name = name;
    }
    public ActivityDto(){
    }
    public String getName(){
        return name;
    }
}
