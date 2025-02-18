package com.steam.reviews.steamreviews.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class App  implements Serializable {

    private int appid;
    private String name;

}
