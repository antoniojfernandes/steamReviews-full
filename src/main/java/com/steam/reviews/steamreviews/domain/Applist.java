package com.steam.reviews.steamreviews.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class Applist implements Serializable {

    private ArrayList<App> apps;

}