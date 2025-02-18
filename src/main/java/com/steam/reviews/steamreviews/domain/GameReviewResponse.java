package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class GameReviewResponse implements Serializable {

    public QuerySummary query_summary;
    public ArrayList<Review> reviews;
    public String cursor;
}