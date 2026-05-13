import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.nio.file.Paths;

public class S3Uploader {
    public static void main(String[] args) {
        String bucketName = "my-java-s3-project-2026"; 
        String fileKey = "hello-cloud.txt";
        String localPath = "C:\\temp\\test.txt"; // ודאי שיש לך קובץ כזה בנתיב הזה!

        S3Client s3 = S3Client.builder()
        		.region(Region.EU_NORTH_1)
                .build();

        try {
            System.out.println("מעלה קובץ לאמזון...");
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build(), Paths.get(localPath));
            System.out.println("ההעלאה הצליחה!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s3.close();
        }
    }
    public static void downloadFile(S3Client s3, String bucketName, String key, String downloadPath) {
        try {
            System.out.println("מוריד את הקובץ מהענן...");
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // כאן אנחנו אומרים ל-SDK לשמור את התוצאה כקובץ בנתיב שבחרנו
            s3.getObject(getObjectRequest, ResponseTransformer.toFile(Paths.get(downloadPath)));
            
            System.out.println("הקובץ ירד בהצלחה ל: " + downloadPath);
            
        } catch (Exception e) {
            System.err.println("שגיאה בהורדה: " + e.getMessage());
        }
    }
}