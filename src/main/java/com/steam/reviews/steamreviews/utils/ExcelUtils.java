package com.steam.reviews.steamreviews.utils;

import com.steam.reviews.steamreviews.domain.QuerySummary;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.steam.reviews.steamreviews.domain.Review;

import java.io.IOException;
import java.io.OutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelUtils {

    public void exportToExcelWithSummary(List<String> criteria, List<Review> reviews, QuerySummary summary, String gameName, OutputStream outputStream) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet reviewsSheet = workbook.createSheet("Reviews for " + gameName);
            setupSheet(reviewsSheet, criteria.size() + 2);
            Row header = reviewsSheet.createRow(0);
            createHeader(header, criteria);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int rowNbr = 1; rowNbr <= reviews.size(); rowNbr++) {
                Row row = reviewsSheet.createRow(rowNbr);
                populateRow(row, reviews.get(rowNbr - 1), style, formatter, criteria);
            }

            Sheet summarySheet = workbook.createSheet("Query Summary for " + gameName);
            Row summaryHeader = summarySheet.createRow(0);
            Cell summaryHeaderCell = summaryHeader.createCell(0);
            summaryHeaderCell.setCellValue("Query Summary");

            Row summaryRow = summarySheet.createRow(1);
            Cell summaryCell = summaryRow.createCell(0);
            summaryCell.setCellValue(summary.toString());

            workbook.write(outputStream);
        }
    }


    private void setupSheet(Sheet sheet, int criteriaSize) {
        for (int rowNbr = 0; rowNbr < criteriaSize; rowNbr++) {
            sheet.setColumnWidth(rowNbr, 6000);
        }
    }

    private void createHeader(Row header, List<String> criteria) {
        for (int i = 0; i < criteria.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(criteria.get(i));
        }
    }

    private void populateRow(Row row, Review review, CellStyle style, DateTimeFormatter formatter, List<String> criteria) {

        Cell cell = row.createCell(criteria.indexOf("Review"));
        cell.setCellValue(review.getReview());
        cell.setCellStyle(style);

        cell = row.createCell(criteria.indexOf("Language"));
        cell.setCellValue(review.getLanguage());
        cell.setCellStyle(style);

        if (criteria.contains("Author's playtime last two weeks")) {
            cell = row.createCell(criteria.indexOf("Author's playtime last two weeks"));
            cell.setCellValue(Math.round(Double.parseDouble(review.getAuthor().getPlaytime_last_two_weeks()) / 6) / 10.0);
            cell.setCellStyle(style);
        }

        if (criteria.contains("Author's last played")) {
            cell = row.createCell(criteria.indexOf("Author's last played"));
            cell.setCellValue(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(review.getAuthor().getLast_played())), ZoneId.systemDefault()).format(formatter));
            cell.setCellStyle(style);
        }

        if (criteria.contains("Does the reviewer play mostly on steam deck")) {
            cell = row.createCell(criteria.indexOf("Does the reviewer play mostly on steam deck"));
            cell.setCellValue(review.getPrimarly_steam_deck());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Developer's response date") && review.getTimestamp_dev_responded() != null) {
            cell = row.createCell(criteria.indexOf("Developer's response date"));
            cell.setCellValue(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(review.getTimestamp_dev_responded())), ZoneId.systemDefault()).format(formatter));
            cell.setCellStyle(style);
        }

        if (criteria.contains("Developer's response")) {
            cell = row.createCell(criteria.indexOf("Developer's response"));
            cell.setCellValue(review.getDeveloper_response());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Reviewer purchased the game on Steam")) {
            cell = row.createCell(criteria.indexOf("Reviewer purchased the game on Steam"));
            cell.setCellValue(review.getSteam_purchase());
            cell.setCellStyle(style);
        }


        if (criteria.contains("Author's playtime forever")) {
            cell = row.createCell(criteria.indexOf("Author's playtime forever"));
            cell.setCellValue(Math.round(Double.parseDouble(review.getAuthor().getPlaytime_forever()) / 6) / 10.0);
            cell.setCellStyle(style);
        }

        if (criteria.contains("Review creation date")) {
            cell = row.createCell(criteria.indexOf("Review creation date"));
            cell.setCellValue(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(review.getTimestamp_created())), ZoneId.systemDefault()).format(formatter));
            cell.setCellStyle(style);
        }

        if (criteria.contains("Review update date")) {
            cell = row.createCell(criteria.indexOf("Review update date"));
            cell.setCellValue(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(review.getTimestamp_updated())), ZoneId.systemDefault()).format(formatter));
            cell.setCellStyle(style);
        }

        if (criteria.contains("Review voted up")) {
            cell = row.createCell(criteria.indexOf("Review voted up"));
            cell.setCellValue(review.getVoted_up());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Review votes up")) {
            cell = row.createCell(criteria.indexOf("Review votes up"));
            cell.setCellValue(review.getVotes_up());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Votes funny")) {
            cell = row.createCell(criteria.indexOf("Votes funny"));
            cell.setCellValue(review.getVotes_funny());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Helpfulness score")) {
            cell = row.createCell(criteria.indexOf("Helpfulness score"));
            cell.setCellValue(review.getWeighted_vote_score());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Reviewer got the game for free")) {
            cell = row.createCell(criteria.indexOf("Reviewer got the game for free"));
            cell.setCellValue(review.getReceived_for_free());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Review during early access")) {
            cell = row.createCell(criteria.indexOf("Review during early access"));
            cell.setCellValue(review.getWritten_during_early_access());
            cell.setCellStyle(style);
        }

        if (criteria.contains("Author's playtime at review") && review.getAuthor().getPlaytime_at_review() != null) {
            cell = row.createCell(criteria.indexOf("Author's playtime at review"));
            cell.setCellValue(Math.round(Double.parseDouble(review.getAuthor().getPlaytime_at_review()) / 6) / 10.0);
            cell.setCellStyle(style);
        }
    }
}