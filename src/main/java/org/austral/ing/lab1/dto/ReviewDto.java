package org.austral.ing.lab1.dto;

public class ReviewDto {
    private String username;
    private String comment;
    private String rating;
    public ReviewDto(String username, String comment, String rating){
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUsername(){
        return username;
    }

    public String getComment(){
        return comment;
    }

    public String getRating(){
        return rating;
    }

}
