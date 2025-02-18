package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameInfo {
    private String name;
    private String imageUrl;
    private int id;
}
