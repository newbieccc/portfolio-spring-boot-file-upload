package com.portfolio.fileupload.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvProcessor {

    public record CsvRow(
            LocalDate bizDate,
            String itemCode, int qty
    ) {
    }

    public static List<CsvRow> parseAndValidate(String storedPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(storedPath))) {

            String header = br.readLine();

            if (header == null) {
                throw new IllegalArgumentException("CSV 헤더가 올바르지 않습니다.");
            }

            header = removeBom(header).trim();

            String[] headerParts = header.split(",", -1);

            if (headerParts.length != 3
                    || !headerParts[0].equals("bizDate")
                    || !headerParts[1].equals("itemCode")
                    || !headerParts[2].equals("qty")) {
                throw new IllegalArgumentException("CSV 헤더가 올바르지 않습니다. (bizDate, itemCode, qty)");
            }


            List<CsvRow> rows = new ArrayList<>();

            String line;
            int lineNo = 1;

            while ((line = br.readLine()) != null) {
                lineNo++;

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length != 3) {
                    throw new IllegalArgumentException("CSV 컬럼 수가 올바르지 않습니다");
                }

                LocalDate bizDate;
                try {
                    bizDate = LocalDate.parse(parts[0].trim());
                } catch (Exception e) {
                    throw new IllegalArgumentException("bizDate 파싱 실패. line=" + lineNo);
                }

                String itemCode = parts[1].trim();
                if (itemCode.isBlank()) {
                    throw new IllegalArgumentException("itemCode 공백 불가. line=" + lineNo);
                }

                int qty;
                try {
                    qty = Integer.parseInt(parts[2].trim());
                } catch (Exception e) {
                    throw new IllegalArgumentException("qty 정수 파싱 시랲. line=" + lineNo);
                }

                if (qty < 0) {
                    throw new IllegalArgumentException("qty는 0 이상이어야 합니다. line=" + lineNo);
                }

                rows.add(new CsvRow(bizDate, itemCode, qty));
            }

            return rows;


        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("CSV 처리중 오류가 발생했습니다.");
        }
    }

    private static String removeBom(String text) {
        if (text != null && !text.isEmpty() && text.charAt(0) == '\uFEFF') {
            return text.substring(1);
        }
        return text;
    }
}
