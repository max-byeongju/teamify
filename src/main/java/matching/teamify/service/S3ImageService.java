package matching.teamify.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageService {

    private final S3Client s3Client;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    @Value("${S3_IMAGE_FOLDER}")
    private String imageFolder;

    public String uploadImage(MultipartFile image) {
        validateImageFile(image);
        String originalFilename = determineFilename(image.getOriginalFilename());
        String extension = extractExtension(originalFilename);

        String s3Key = imageFolder + UUID.randomUUID().toString() + "." + extension;

        try (InputStream inputStream = image.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(image.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, image.getSize()));
            return s3Key;

        } catch (IOException e) {
            throw new RuntimeException("이미지 파일을 읽는데 실패했습니다.", e);
        } catch (AwsServiceException | SdkClientException e) {
            throw new RuntimeException("S3 통신 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteImage(String s3Key) {
        if (s3Key == null || s3Key.trim().isEmpty()) {
            log.warn("유효하지 않은 키로 삭제 시도: {}", s3Key);
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("S3 이미지 '{}' 삭제 중 오류 발생", s3Key, e);
        }
    }

    public String getImageUrl(String s3Key) {
        if (s3Key == null || s3Key.trim().isEmpty()) {
            return null;
        }
        try {
            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            return s3Client.utilities().getUrl(request).toString();
        } catch (AwsServiceException | SdkClientException e) {
            log.error("키 '{}'에 대한 URL 생성 중 오류 발생", s3Key, e);
            return null;
        }
    }

    private void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있거나 null입니다.");
        }
    }

    private String determineFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return originalFilename;
    }

    private String extractExtension(String filename) {
        try {
            int lastDot = filename.lastIndexOf(".");
            if (lastDot < 0 || lastDot == filename.length() - 1) {
                throw new IllegalArgumentException("유효하지 않거나 누락된 파일 확장자입니다.");
            }
            return filename.substring(lastDot + 1).toLowerCase();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("파일 확장자를 추출할 수 없습니다.");
        }
    }
}