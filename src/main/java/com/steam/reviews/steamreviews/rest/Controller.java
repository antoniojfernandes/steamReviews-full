package com.steam.reviews.steamreviews.rest;

import com.steam.reviews.steamreviews.domain.GameInfo;
import com.steam.reviews.steamreviews.service.ReviewService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
public class Controller {

    private final ReviewService reviewService;

    public Controller(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/download-reviews")
    public ResponseEntity<ByteArrayResource> downloadReviews(@RequestParam String gameName,
                                                             @RequestParam int gameId,
                                                             @RequestParam String criteriaList,
                                                             @RequestParam String selectedLang,
                                                             @RequestParam String selectedFilter,
                                                             @RequestParam String selectedreviewType,
                                                             @RequestParam int minChars,
                                                             @RequestParam int maxChars,
                                                             @RequestParam int pages) throws IOException {
        List<String> criteria = Arrays.asList(criteriaList.split(","));

        ByteArrayResource resource = reviewService.fetchAndExportReviews(gameName,
                gameId,
                criteria,
                selectedLang,
                selectedFilter,
                selectedreviewType,
                minChars,
                maxChars,
                pages
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/find-game-by-name")
    public ResponseEntity<List<GameInfo>> downloadReviews(@RequestParam String game) {
        List<GameInfo> gameInfo = reviewService.getGameInfoByName(game);

        return ResponseEntity.ok()
                .body(gameInfo);
    }

}