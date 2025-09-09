package com.easytiwu.serviceupload.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author sheny
 */
public class UploadRequestDTO {
    private String bankName;
    private String description;
    private MultipartFile file;

    // Getters and Setters
    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}