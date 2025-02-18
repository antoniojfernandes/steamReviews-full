package com.steam.reviews.steamreviews.service;

import com.steam.reviews.steamreviews.domain.*;
import com.steam.reviews.steamreviews.utils.ExcelUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final String GAME_REVIEW_URL = "https://store.steampowered.com/appreviews/%GAME_ID%?json=1&language=%LANG%&filter=%FILTER%&review_type=%REVIEW_TYPE%&num_per_page=100&cursor=";
    private static final String APP_LIST_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v0002/?key=STEAMKEY&format=json";
    private static final String GAME_INFO_URL = "https://store.steampowered.com/api/appdetails?appids=";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ExcelUtils excelUtils = new ExcelUtils();


    public ByteArrayResource fetchAndExportReviews(String gameName, int gameId, List<String> criteriaList, String selectedLang, String selectedFilter, String selectedreviewType, int minChars, int maxChars, int pages) throws IOException {


        String name = gameName.replaceAll("[\\\\/:*?\"<>|']", "_").replaceAll(" ", "_");

        String requestUrl = GAME_REVIEW_URL.replace("%GAME_ID%", String.valueOf(gameId)).replace("%LANG%", selectedLang).replace("%FILTER%", selectedFilter).replace("%REVIEW_TYPE%", selectedreviewType);
        FetchResult fetchResult = fetchReviewsAndSummary(requestUrl, minChars, maxChars, pages);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelUtils.exportToExcelWithSummary(criteriaList, fetchResult.getReviews(), fetchResult.getQuerySummary(), gameName, outputStream);

        return new ByteArrayResource(outputStream.toByteArray()) {
            @Override
            public String getFilename() {
                return gameName + ".xlsx";
            }
        };
    }

    private FetchResult fetchReviewsAndSummary(String requestUrl, int minChars, int maxChars, int pages) {
        String cursor = "*";
        List<Review> reviews = new ArrayList<>();
        QuerySummary querySummary = null;

        int pageNbr = 1;

        while (pages > 0) {
            System.out.println("pageNbr: " + pageNbr++);
            String url = requestUrl + cursor;
            URI uri = UriComponentsBuilder.fromUriString(url).build(true).toUri();
            GameReviewResponse response = restTemplate.getForObject(uri, GameReviewResponse.class);
            if (response != null && response.getReviews() != null && !response.getReviews().isEmpty()) {
                reviews.addAll(response.getReviews());
                pages--;
                cursor = URLEncoder.encode(response.getCursor(), StandardCharsets.UTF_8);
                if (querySummary == null) {
                    querySummary = response.getQuery_summary();
                }
            } else {
                break;
            }
        }

        if (maxChars == -1) {
            reviews = reviews.stream().filter(review -> review.getReview().length() > minChars).collect(Collectors.toList());
        } else {
            reviews = reviews.stream().filter(review -> review.getReview().length() > minChars && review.getReview().length() < maxChars).collect(Collectors.toList());
        }

        assert querySummary != null;
        querySummary.setTotal_in_file(reviews.size());
        return new FetchResult(reviews, querySummary);
    }


    public List<GameInfo> getGameInfoByName(String gameName) {
        // Fetch the list of apps
        AppListResponse appListResponse = restTemplate.getForObject(APP_LIST_URL, AppListResponse.class);
        if (appListResponse == null || appListResponse.getApplist() == null) {
            return Collections.emptyList();
        }

        // Filter games by name (case-insensitive)
        List<App> games = appListResponse.getApplist().getApps().stream().filter(app -> app.getName().toLowerCase().contains(gameName.toLowerCase())).toList();

        // Fetch and map game info
        return games.stream().map(this::fetchGameInfo).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private Optional<GameInfo> fetchGameInfo(App game) {
        try {
            // Fetch game info from the API
            Map<String, Map<String, Map<String, String>>> response = restTemplate.getForObject(GAME_INFO_URL + game.getAppid(), LinkedHashMap.class);

            // Extract name and image URL using Optional to avoid null checks
            return Optional.ofNullable(response).map(resp -> resp.get(String.valueOf(game.getAppid()))).map(data -> data.get("data")).map(data -> new GameInfo(data.get("name"), data.get("header_image"), game.getAppid()));
        } catch (Exception e) {
            // Log the error and return an empty Optional
            System.err.println("Error fetching game info for appId: " + game.getAppid() + ", Error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
