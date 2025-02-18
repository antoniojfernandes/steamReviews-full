package com.steam.reviews.steamreviews.service;

import com.steam.reviews.steamreviews.domain.*;
import com.steam.reviews.steamreviews.utils.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.steam.reviews.steamreviews.utils.Utils.stringToDate;
import static com.steam.reviews.steamreviews.utils.Utils.timeStampToLocalDate;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private static final String GAME_REVIEW_URL = "https://store.steampowered.com/appreviews/%GAME_ID%?json=1&language=%LANG%&filter=%FILTER%&review_type=%REVIEW_TYPE%&num_per_page=100&cursor=";
    private static final String APP_LIST_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v0002/?key=STEAMKEY&format=json";
    private static final String GAME_INFO_URL = "https://store.steampowered.com/api/appdetails?appids=";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExcelUtils excelUtils = new ExcelUtils();


    public ByteArrayResource fetchAndExportReviews(String gameName, int gameId, List<String> criteriaList, String selectedLang, String selectedFilter, String selectedReviewType, int minChars, int maxChars, int pages, String startDate, String endDate) throws IOException {
        String sanitizedGameName = sanitizeGameName(gameName);
        String requestUrl = buildReviewUrl(gameId, selectedLang, selectedFilter, selectedReviewType);
        FetchResult fetchResult = fetchReviewsAndSummary(requestUrl, minChars, maxChars, pages, startDate, endDate);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelUtils.exportToExcelWithSummary(criteriaList, fetchResult.getReviews(), fetchResult.getQuerySummary(), gameName, outputStream);

        return new ByteArrayResource(outputStream.toByteArray()) {
            @Override
            public String getFilename() {
                return sanitizedGameName + ".xlsx";
            }
        };
    }

    private String sanitizeGameName(String gameName) {
        return gameName.replaceAll("[\\\\/:*?\"<>|']", "_").replaceAll(" ", "_");
    }

    private String buildReviewUrl(int gameId, String selectedLang, String selectedFilter, String selectedReviewType) {
        return GAME_REVIEW_URL.replace("%GAME_ID%", String.valueOf(gameId))
                .replace("%LANG%", selectedLang)
                .replace("%FILTER%", selectedFilter)
                .replace("%REVIEW_TYPE%", selectedReviewType);
    }

    private FetchResult fetchReviewsAndSummary(String requestUrl, int minChars, int maxChars, int pages, String startDate, String endDate) {
        String cursor = "*";
        List<Review> reviews = new ArrayList<>();
        QuerySummary querySummary = null;

        while (pages > 0) {
            logger.info("{} pages left", pages);
            String url = requestUrl + cursor;
            URI uri = UriComponentsBuilder.fromUriString(url).build(true).toUri();
            GameReviewResponse response = restTemplate.getForObject(uri, GameReviewResponse.class);

            if (response == null || response.getReviews() == null || response.getReviews().isEmpty()) {
                break;
            }

            reviews.addAll(response.getReviews());
            pages--;
            cursor = URLEncoder.encode(response.getCursor(), StandardCharsets.UTF_8);
            if (querySummary == null) {
                querySummary = response.getQuery_summary();
            }
        }

        reviews = filterReviewsByLength(reviews, minChars, maxChars);

        reviews = filterReviewsByDate(reviews, startDate, endDate);

        if (querySummary != null) {
            querySummary.setTotal_in_file(reviews.size());
        }

        return new FetchResult(reviews, querySummary);
    }

    private List<Review> filterReviewsByLength(List<Review> reviews, int minChars, int maxChars) {
        return reviews.stream()
                .filter(review -> review.getReview().length() > minChars && (maxChars == -1 || review.getReview().length() < maxChars))
                .collect(Collectors.toList());
    }

    private List<Review> filterReviewsByDate(List<Review> reviews, String startDate, String endDate) {
        return reviews.stream()
                .filter(review -> timeStampToLocalDate(review.getTimestamp_created()).isAfter(stringToDate(startDate))
                        && timeStampToLocalDate(review.getTimestamp_created()).isBefore(stringToDate(endDate)))
                .collect(Collectors.toList());
    }

    public List<GameInfo> getGameInfoByName(String gameName) {
        return Optional.ofNullable(restTemplate.getForObject(APP_LIST_URL, AppListResponse.class))
                .map(AppListResponse::getApplist)
                .map(Applist::getApps)
                .orElse(new ArrayList<>())
                .stream()
                .filter(app -> app.getName().toLowerCase().contains(gameName.toLowerCase()))
                .map(this::fetchGameInfo)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<GameInfo> fetchGameInfo(App game) {
        try {
            Map<String, Map<String, Map<String, String>>> response = restTemplate.getForObject(GAME_INFO_URL + game.getAppid(), LinkedHashMap.class);
            return Optional.ofNullable(response)
                    .map(resp -> resp.get(String.valueOf(game.getAppid())))
                    .map(data -> data.get("data"))
                    .map(data -> new GameInfo(data.get("name"), data.get("header_image"), game.getAppid()));
        } catch (Exception e) {
            logger.error("Error fetching game info for appId: {}, Error: {}", game.getAppid(), e.getMessage());
            return Optional.empty();
        }
    }
}