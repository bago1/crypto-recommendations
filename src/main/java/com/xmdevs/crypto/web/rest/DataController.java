package com.xmdevs.crypto.web.rest;

import com.xmdevs.crypto.util.CryptoCsvReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {
    private final CryptoCsvReader cryptoCsvReader;

    @PostMapping("/upload")
    public String uploadCryptoData(@RequestParam("file") MultipartFile file) {
        try {
            cryptoCsvReader.processUploadedFile(file);
            return "File uploaded and processed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing the uploaded file.";
        }
    }
}
