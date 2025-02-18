package com.steam.reviews.steamreviews.domain;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class FetchResult  implements Serializable {
    private final List<Review> reviews;
    private final QuerySummary querySummary;

    public FetchResult(List<Review> reviews, QuerySummary querySummary) {
        this.reviews = reviews;
        this.querySummary = querySummary;
    }

}