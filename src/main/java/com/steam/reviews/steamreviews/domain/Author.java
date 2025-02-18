package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Author implements Serializable {

    public String playtime_forever;
    public String playtime_last_two_weeks;
    public String playtime_at_review;
    public String last_played;
}