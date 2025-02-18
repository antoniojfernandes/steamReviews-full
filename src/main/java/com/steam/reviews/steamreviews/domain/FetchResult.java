package com.steam.reviews.steamreviews.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class FetchResult {
    private final List<Review> reviews;
    private final QuerySummary querySummary;

    public FetchResult(List<Review> reviews, QuerySummary querySummary) {
        this.reviews = reviews;
        this.querySummary = querySummary;
    }

}