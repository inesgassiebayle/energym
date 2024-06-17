package org.austral.ing.lab1.dto;

public class LessonDto {
    private String id;
    private String name;
    private String date;
    private String time;
    private String room;
    private String activity;
    private String professor;

    public LessonDto(String id, String name, String date, String time, String room, String activity, String professor) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.room = room;
        this.activity = activity;
        this.professor = professor;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getRoom() {
        return room;
    }
    public String getActivity() {
        return activity;
    }
    public String getProfessor() {
        return professor;
    }
}
