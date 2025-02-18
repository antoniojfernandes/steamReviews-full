package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class Review implements Serializable {

    private String recommendationid;
    private Author author;
    private String language;
    private String review;
    private Long timestamp_created;
    private Long timestamp_updated;
    private String voted_up;
    private String votes_up;
    private String weighted_vote_score;
    private String steam_purchase;
    private String received_for_free;
    private String written_during_early_access;
    private String votes_funny;
    private String developer_response;
    private Long timestamp_dev_responded;
    private String primarly_steam_deck;
    private String comment_count;
}