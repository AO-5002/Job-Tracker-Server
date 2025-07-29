package org.example.server.controllers;

import org.example.server.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        s3Service.uploadFile(file);
        return ResponseEntity.ok("File uploaded");
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable("filename") String filename) throws IOException {
        byte[] data = s3Service.downloadFile(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,  "inline; filename=\"" + filename + "\"").body(data);
    }

}
