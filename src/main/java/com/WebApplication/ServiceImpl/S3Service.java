package com.WebApplication.ServiceImpl;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // ✅ Upload a single image to S3
    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = "New_website_gallery/" + Instant.now().getEpochSecond() + "_" + file.getOriginalFilename();

        // Save the file temporarily
        java.nio.file.Path tempPath = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempPath.toFile());

        // Upload file to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempPath));

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    // ✅ Upload multiple images to S3
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = uploadImage(file);  // Reuse single upload method
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }

//    // ✅ Delete image from S3
//    public void deleteImage(String fileUrl) {
//        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName)
//                .key("website_gallery/" + fileName)
//                .build();
//
//        s3Client.deleteObject(deleteObjectRequest);
//    }


    // ✅ Delete image from S3
    public void deleteImage(String fileUrl) {
        try {
            // Extract the key (path inside S3 bucket)
            String key = fileUrl.substring(fileUrl.indexOf("website_gallery/"));

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key) // Use extracted key instead of full URL
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("Successfully deleted image from S3: " + key);

        } catch (Exception e) {
            System.err.println("Failed to delete image from S3: " + e.getMessage());
            throw new RuntimeException("Failed to delete image from S3", e);
        }
    }

}
