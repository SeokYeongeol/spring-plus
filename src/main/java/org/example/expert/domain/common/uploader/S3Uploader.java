package org.example.expert.domain.common.uploader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${folder.profile}")
    private String folder;
    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file) {
        String filename = uploadFolder(folder) + generateFilename(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, filename, file.getInputStream(), objectMetadata);
        } catch(IOException ie) {
            throw new InvalidRequestException("파일 업로드에 실패했습니다");
        }

        return amazonS3.getUrl(bucket, filename).toString();
    }

    private String generateFilename(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private String uploadFolder(String folder) {
        return folder + "/";
    }
}
