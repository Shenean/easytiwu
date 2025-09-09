package com.easytiwu.serviceupload.controller;

import com.easytiwu.serviceupload.service.FileParsingService;
import com.easytiwu.serviceupload.service.LargeModelService;
import com.easytiwu.serviceupload.service.DataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;


/**
 * @author sheny
 */
@RestController
@RequestMapping("")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final FileParsingService fileParsingService;
    private final LargeModelService largeModelService;
    private final DataImportService dataImportService;

    public FileUploadController(FileParsingService fileParsingService,
                                LargeModelService largeModelService,
                                DataImportService dataImportService) {
        this.fileParsingService = fileParsingService;
        this.largeModelService = largeModelService;
        this.dataImportService = dataImportService;
    }

    /**
     * Endpoint to upload a question bank file along with metadata.
     */
    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadQuestionBank(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestPart("file") MultipartFile file) {
        try {
            // 1. Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded or file is empty.");
            }
            if (!fileParsingService.isSupportedFile(filename)) {
                return ResponseEntity.badRequest().body("Unsupported file format. Only PDF, Word (.docx/.doc), or TXT are allowed.");
            }

            // 2. Parse file to extract text content
            String textContent = fileParsingService.extractText(file);
            logger.info("Extracted text content from file '{}', length={} characters", filename, textContent.length());

            // 3. Call large model service to get questions JSON
            String questionsJson = largeModelService.generateQuestionsJson(textContent);
            logger.info("Received JSON output from LLM (length={} chars). Starting import to DB.", questionsJson.length());

            // 4. Import the questions into database
            dataImportService.importQuestionsFromJson(name, description, questionsJson);

            // 5. Return success response
            return ResponseEntity.ok("Question bank uploaded and processed successfully.");
        } catch (Exception e) {
            logger.error("Error processing upload: {}", e.getMessage(), e);
            // Return internal server error with generic message (specifics are logged)
            return ResponseEntity.status(500).body("Failed to process the file. Please try again later.");
        }
    }
}