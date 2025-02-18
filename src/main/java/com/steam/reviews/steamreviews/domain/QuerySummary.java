package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class QuerySummary implements Serializable {

    public String review_score_desc;
    public String total_positive;
    public String total_negative;
    public String total_reviews;
    public int total_in_file;

}