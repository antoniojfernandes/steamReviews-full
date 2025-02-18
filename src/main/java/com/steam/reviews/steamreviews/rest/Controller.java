package com.steam.reviews.steamreviews.rest;

import com.steam.reviews.steamreviews.domain.GameInfo;
import com.steam.reviews.steamreviews.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private final ReviewService reviewService;

    public Controller(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/download-reviews")
    public ResponseEntity<?> downloadReviews(
            @RequestParam String gameName,
            @RequestParam int gameId,
            @RequestParam String criteriaList,
            @RequestParam String selectedLang,
            @RequestParam String selectedFilter,
            @RequestParam String selectedReviewType,
            @RequestParam int minChars,
            @RequestParam int maxChars,
            @RequestParam int pages,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            // Validate input parameters
            if (gameName == null || gameName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Game name cannot be empty.");
            }
            if (minChars < 0 || maxChars < -1 || pages <= 0) {
                return ResponseEntity.badRequest().body("Invalid input parameters: minChars, maxChars, or pages.");
            }

            // Split criteriaList into a list
            List<String> criteria = Arrays.asList(criteriaList.split(","));

            // Fetch and export reviews
            ByteArrayResource resource = reviewService.fetchAndExportReviews(
                    gameName,
                    gameId,
                    criteria,
                    selectedLang,
                    selectedFilter,
                    selectedReviewType,
                    minChars,
                    maxChars,
                    pages,
                    startDate,
                    endDate
            );

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (IOException e) {
            logger.error("Error while downloading reviews for game: {}", gameName, e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request.");
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/find-game-by-name")
    public ResponseEntity<?> findGameByName(@RequestParam String game) {
        try {
            // Validate input
            if (game == null || game.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Game name cannot be empty.");
            }

            // Fetch game info
            List<GameInfo> gameInfo = reviewService.getGameInfoByName(game);

            if (gameInfo.isEmpty()) {
                return ResponseEntity.ok().body("No games found with the provided name.");
            }

            return ResponseEntity.ok().body(gameInfo);

        } catch (Exception e) {
            logger.error("Error while finding game by name: {}", game, e);
            return ResponseEntity.internalServerError().body("An error occurred while processing your request.");
        }
    }
}