package org.example.server.services;

import lombok.RequiredArgsConstructor;
import org.example.server.exceptions.file.FileNotValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public void uploadFile(MultipartFile file, String folderDestination) throws FileNotValid {
        try {
            String fileName = file.getOriginalFilename();
            String fullPath = folderDestination + fileName;

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fullPath)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
        } catch (IOException e) {
            throw new FileNotValid("Failed to read file bytes: " + e.getMessage());
        }
    }

    public byte[] downloadFile(String fileName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(request);

        return objectAsBytes.asByteArray();
    }









}
