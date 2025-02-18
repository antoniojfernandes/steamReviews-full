package com.steam.reviews.steamreviews.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class GameInfo  implements Serializable {
    private String name;
    private String imageUrl;
    private int id;
}
