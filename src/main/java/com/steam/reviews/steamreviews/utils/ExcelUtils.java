package com.steam.reviews.steamreviews.utils;

import com.steam.reviews.steamreviews.domain.QuerySummary;
import com.steam.reviews.steamreviews.domain.Review;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.steam.reviews.steamreviews.utils.Utils.formatTimestamp;
import static com.steam.reviews.steamreviews.utils.Utils.parsePlaytime;

public class ExcelUtils {

    public void exportToExcelWithSummary(List<String> criteria, List<Review> reviews, QuerySummary summary, String gameName, OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Create the Reviews sheet
            Sheet reviewsSheet = workbook.createSheet("Reviews for " + gameName);
            setupSheet(reviewsSheet, criteria.size() + 2);
            createHeaderRow(reviewsSheet.createRow(0), criteria);

            // Create a reusable cell style
            CellStyle cellStyle = createWrapTextStyle(workbook);

            // Populate the Reviews sheet with review data
            populateReviewsSheet(reviewsSheet, reviews, criteria, cellStyle);

            // Create the Summary sheet
            Sheet summarySheet = workbook.createSheet("Query Summary for " + gameName);
            createSummarySheet(summarySheet, summary);

            // Write the workbook to the output stream
            workbook.write(outputStream);
        }
    }

    private void setupSheet(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.setColumnWidth(i, 6000);
        }
    }

    private void createHeaderRow(Row headerRow, List<String> criteria) {
        for (int i = 0; i < criteria.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(criteria.get(i));
        }
    }

    private CellStyle createWrapTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        return style;
    }

    private void populateReviewsSheet(Sheet sheet, List<Review> reviews, List<String> criteria, CellStyle cellStyle) {
        // Map criteria to their corresponding data extraction functions
        Map<String, Function<Review, Object>> criteriaMappers = createCriteriaMappers();

        for (int rowIndex = 0; rowIndex < reviews.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1); // +1 to skip the header row
            Review review = reviews.get(rowIndex);

            for (int colIndex = 0; colIndex < criteria.size(); colIndex++) {
                String criterion = criteria.get(colIndex);
                Function<Review, Object> mapper = criteriaMappers.get(criterion);

                if (mapper != null) {
                    Object value = mapper.apply(review);
                    createCell(row, colIndex, value, cellStyle);
                }
            }
        }
    }

    private Map<String, Function<Review, Object>> createCriteriaMappers() {
        Map<String, Function<Review, Object>> criteriaMappers = new HashMap<>();
        criteriaMappers.put("Review", Review::getReview);
        criteriaMappers.put("Language", Review::getLanguage);
        criteriaMappers.put("Does the reviewer play mostly on steam deck", Review::getPrimarly_steam_deck);
        criteriaMappers.put("Developer's response", Review::getDeveloper_response);
        criteriaMappers.put("Reviewer purchased the game on Steam", Review::getSteam_purchase);
        criteriaMappers.put("Review voted up", Review::getVoted_up);
        criteriaMappers.put("Review votes up", Review::getVotes_up);
        criteriaMappers.put("Votes funny", Review::getVotes_funny);
        criteriaMappers.put("Helpfulness score", Review::getWeighted_vote_score);
        criteriaMappers.put("Reviewer got the game for free", Review::getReceived_for_free);
        criteriaMappers.put("Review during early access", Review::getWritten_during_early_access);
        criteriaMappers.put("Developer's response date", review -> formatTimestamp(review.getTimestamp_dev_responded()));
        criteriaMappers.put("Author's last played", review -> formatTimestamp(review.getAuthor().getLast_played()));
        criteriaMappers.put("Author's playtime at review", review -> parsePlaytime(review.getAuthor().getPlaytime_at_review()));
        criteriaMappers.put("Author's playtime last two weeks", review -> parsePlaytime(review.getAuthor().getPlaytime_last_two_weeks()));
        criteriaMappers.put("Author's playtime forever", review -> parsePlaytime(review.getAuthor().getPlaytime_forever()));
        criteriaMappers.put("Review creation date", review -> formatTimestamp(review.getTimestamp_created()));
        criteriaMappers.put("Review update date", review -> formatTimestamp(review.getTimestamp_updated()));

        return criteriaMappers;
    }

    private void createCell(Row row, int colIndex, Object value, CellStyle cellStyle) {
        Cell cell = row.createCell(colIndex);
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            }
        }
        cell.setCellStyle(cellStyle);
    }

    private void createSummarySheet(Sheet sheet, QuerySummary summary) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Query Summary");

        Row summaryRow = sheet.createRow(1);
        summaryRow.createCell(0).setCellValue(summary.toString());
    }
}