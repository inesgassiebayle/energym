package org.austral.ing.lab1.dto;

public class ReviewModificationDto {
    String id;
    String comment;
    String rating;

    public ReviewModificationDto(String id, String comment, String rating) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
    }

    public Long getId() {
        if (id == null) {
            return null;
        }
        return Long.parseLong(id);
    }

    public String getComment(){
        return comment;
    }

    public Integer getRating() {
        if (rating == null) {
            return null;
        }
        return Integer.parseInt(rating);
    }
}
