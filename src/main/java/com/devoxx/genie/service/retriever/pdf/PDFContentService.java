package com.devoxx.genie.service.retriever.pdf;

import com.devoxx.genie.service.util.StorageService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class PDFContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFContentService.class);
    private final StorageService storageService;

    public PDFContentService(StorageService storageService) {
        this.storageService = storageService;
    }

    public String getContent(MultipartFile multipartFile) throws IOException {
        LOGGER.debug("Getting content from PDF: {}", multipartFile.getOriginalFilename());

        Path path = storageService.storeMultipartFile(multipartFile);

        StringBuilder stringBuilder = new StringBuilder();

        try (PDDocument document = Loader.loadPDF(path.toFile())) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }

            PDFTextStripper stripper = new PDFTextStripper();

            // This example uses sorting, but in some cases it is more useful to switch it off,
            // e.g. in some files with columns where the PDF content stream respects the
            // column order.
            stripper.setSortByPosition(true);
            for (int p = 1; p <= document.getNumberOfPages(); ++p) {
                // Set the page interval to extract. If you don't, then all pages would be extracted.
                stripper.setStartPage(p);
                stripper.setEndPage(p);

                // let the magic happen
                String text = stripper.getText(document);

                // do some nice output with a header
                String pageStr = String.format("page %d:", p);
                stringBuilder.append(pageStr);

                stringBuilder.append("-".repeat(pageStr.length()));
                stringBuilder.append(text.trim());
            }
        }

        storageService.deleteAll(); // Delete the file after processing

        return stringBuilder.toString();
    }
}
