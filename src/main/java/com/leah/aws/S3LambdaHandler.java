package com.leah.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

public class S3LambdaHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        // שליפת הרשומה הראשונה מהאירוע
        S3EventNotificationRecord record = s3event.getRecords().get(0);
        
        String srcBucket = record.getS3().getBucket().getName();
        String srcKey = record.getS3().getObject().getUrlDecodedKey();

        // הדפסה ללוגים של CloudWatch
        context.getLogger().log("כאן למדא! קובץ חדש הגיע ל-S3: " + srcKey + " מהבאקט: " + srcBucket);

        return "Processed successfully";
    }
}