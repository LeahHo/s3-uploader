package com.leah.aws;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainApp {
    public static void main(String[] args) {
        String myBucket = "my-java-s3-project-2026";
        
        try (S3Service s3Service = new S3Service(myBucket)) {
            
            // 1. הגדרת שם הקובץ והנתיב שלו (יווצר בתיקייה שבה האפליקציה רצה כרגע)
            String fileName = "docker-test-file.txt";
            Path tempFilePath = Paths.get(fileName);
            
            // 2. יצירת הקובץ בפועל וכתיבת תוכן לתוכו
            Files.writeString(tempFilePath, "Hello from a Linux Docker Container! Leah's App is cloud-native.");
            System.out.println("Created temp file at: " + tempFilePath.toAbsolutePath());
            
            // 3. העלאת הקובץ ל-S3 תוך שימוש בנתיב המוחלט ש-Java זיהתה
            s3Service.uploadFile(fileName, tempFilePath.toAbsolutePath().toString());
            
            // 4. הצגת רשימת הקבצים כדי לוודא הצלחה
            s3Service.listFiles();
            
            System.out.println("All operations completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}