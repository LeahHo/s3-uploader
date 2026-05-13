package com.leah.aws;

import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Paths;
import java.util.List;

/**
 * S3Service - Production-grade service for AWS S3 with CloudWatch Monitoring.
 */
public class S3Service implements AutoCloseable {

    private final S3Client s3Client;
    private final CloudWatchClient cwClient;
    private final String bucketName;

    public S3Service(String bucketName) {
        this.bucketName = bucketName;
        // אתחול הלקוחות עם ה-Region הנכון (שטוקהולם)
        this.s3Client = S3Client.builder().region(Region.EU_NORTH_1).build();
        this.cwClient = CloudWatchClient.builder().region(Region.EU_NORTH_1).build();
    }

    // העלאת קובץ עם דיווח על הצלחה/כישלון
    public void uploadFile(String key, String localPath) {
        try {
            System.out.println("Uploading: " + key);
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(), Paths.get(localPath));
            
            sendMetric("UploadSuccess", 1.0);
        } catch (Exception e) {
            sendMetric("UploadError", 1.0);
            throw e;
        }
    }

    // הורדת קובץ
    public void downloadFile(String key, String downloadPath) {
        try {
            System.out.println("Downloading: " + key);
            s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build(), ResponseTransformer.toFile(Paths.get(downloadPath)));
            
            sendMetric("DownloadSuccess", 1.0);
        } catch (Exception e) {
            sendMetric("DownloadError", 1.0);
            throw e;
        }
    }

    // הצגת רשימת קבצים (מה שהיה חסר לך!)
    public void listFiles() {
        System.out.println("Listing objects in bucket: " + bucketName);
        ListObjectsResponse listObjects = s3Client.listObjects(ListObjectsRequest.builder()
                .bucket(bucketName)
                .build());
        
        List<S3Object> objects = listObjects.contents();
        if (objects.isEmpty()) {
            System.out.println("The bucket is empty.");
        } else {
            objects.forEach(obj -> System.out.println("- " + obj.key() + " (Size: " + obj.size() + " bytes)"));
        }
    }

    // מחיקת קובץ
    public void deleteFile(String key) {
        System.out.println("Deleting: " + key);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    // מתודה פנימית לשליחת Metrics ל-CloudWatch
    private void sendMetric(String metricName, Double value) {
        try {
            MetricDatum datum = MetricDatum.builder()
                    .metricName(metricName)
                    .unit(StandardUnit.COUNT)
                    .value(value)
                    .build();

            PutMetricDataRequest request = PutMetricDataRequest.builder()
                    .namespace("Cellebrite/S3Uploader")
                    .metricData(datum)
                    .build();

            cwClient.putMetricData(request);
        } catch (Exception e) {
            System.err.println("Failed to send metric to CloudWatch: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (s3Client != null) s3Client.close();
        if (cwClient != null) cwClient.close();
        System.out.println("AWS Resources released.");
    }
}