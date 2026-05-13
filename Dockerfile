# 1. מתחילים מסביבת בסיס רשמית וקלה של Java 21
FROM eclipse-temurin:21-jre-alpine

# 2. מגדירים את תיקיית העבודה בתוך הקונטיינר
WORKDIR /app

# 3. מעתיקים את ה-Fat JAR שיצרנו פנימה
COPY target/s3-uploader-0.0.1-SNAPSHOT.jar app.jar

# 4. מגדירים מה קורה כשהקונטיינר עולה
ENTRYPOINT ["java", "-jar", "app.jar"]