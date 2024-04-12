package com.devoxx.genie.service.retriever.poi;


import com.devoxx.genie.service.util.StorageService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


@Service
public class PoiContentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoiContentService.class);
    private final StorageService storageService;

    public PoiContentService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String getContent(MultipartFile multipartFile) throws IOException {
        LOGGER.debug("Getting content from Excel: {}", multipartFile.getOriginalFilename());

        Path path = storageService.storeMultipartFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename != null) {
            StringBuilder stringBuilder = new StringBuilder();

            try (FileInputStream fis = new FileInputStream(path.toFile())) {
                if (originalFilename.toLowerCase().endsWith(".xlsx")) {
                    extractExcelContent(new XSSFWorkbook(fis), stringBuilder);
                } else if (originalFilename.toLowerCase().endsWith(".xls")) {
                    extractExcelContent(new HSSFWorkbook(fis), stringBuilder);
                } else if (originalFilename.toLowerCase().endsWith(".docx")) {
                    extractWordContent(fis, stringBuilder);
                }
            } catch (IOException e) {
                LOGGER.error("Error processing file: {}", e.getMessage());
                throw e;
            }

            // Delete the file after processing
            storageService.deleteAll();

            return stringBuilder.toString();
        } else {
            LOGGER.warn("Unsupported file type");
        }

        return "Unsupported file type";
    }

    private static void extractExcelContent(Workbook workbook, StringBuilder stringBuilder) {
        if (workbook != null) {
            int numberOfSheets = workbook.getNumberOfSheets();

            for (int i = 0; i < numberOfSheets; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                stringBuilder.append(cell.getStringCellValue().trim()).append(" ");
                                break;
                            case NUMERIC:
                                stringBuilder.append(cell.getNumericCellValue()).append(" ");
                                break;
                            default:
                                LOGGER.warn("Unsupported cell type: {}", cell.getCellType());
                                break;
                        }
                    }
                    stringBuilder.append("\n"); // New line for each row
                }
            }
        }
    }

    private static void extractWordContent(FileInputStream fis, StringBuilder stringBuilder) throws IOException {
        XWPFDocument document = new XWPFDocument(fis);
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        document.close();

        for (XWPFParagraph paragraph : paragraphs) {
            stringBuilder.append(paragraph.getText()).append("\n");
        }
    }

}
