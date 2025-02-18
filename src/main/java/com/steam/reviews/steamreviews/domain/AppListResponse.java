package com.steam.reviews.steamreviews.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppListResponse  implements Serializable {
    private Applist applist;
}