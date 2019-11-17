package com.example.galleryproject.Model;

import android.os.Parcelable;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


public abstract class Image implements Parcelable {
    private File file;
    private LocalDateTime creationTime;

    private double latitude;
    private double longitude;

    public Image() {
        this("");
    }

    public Image(String path) {
        this(new File(path));
    }

    public Image(File file) {
        this.file = file;
        this.creationTime = extractCreationTime();
        this.latitude = ExifLocationExtractor.getLatitude(this.file);
        this.longitude = ExifLocationExtractor.getLongitude(this.file);
    }

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getFilePath() {
        return this.file.toPath().toString();
    }

    public void setFilePath(String path) {
        this.file = new File(path);
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setCreationTime(LocalDateTime datetime) {
        this.creationTime = datetime;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public void setLongitude(double lng) {
        this.longitude = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof Image) {
            Image i = (Image) o;

            String s1 = this.file.toPath().toString();
            String so = i.file.toPath().toString();
            return so.equals(s1);
        }

        if (o instanceof File) {
            File f = (File) o;
            Log.e("Image", f.toPath().toString());
            Log.e("Image", this.file.toPath().toString());
            return this.file.equals(f);
        }

        return false;
    }


    private LocalDateTime extractCreationTime() {
        if (hasExifCreationTime()) {
            return getExifCreationTime();
        }

        return getFileCreationTime();
    }

    // EXIF 메타 데이터에서 촬영시각 가져오기
    // 출력데이터 형식 (삼성 기준 - YYYY:MM:DD HH:mm:ss)
    private LocalDateTime getExifCreationTime() {
        ExifInterface exif = getExif();
        if (exif != null) {
            String creationTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            return ExifDatetimeExtractor.getLocalDateTime(creationTime, "yyyy:MM:dd HH:mm:ss");
        }

        return null;
    }

    // 파일 생성 시각 (수정 시각)에서 가져오기
    private LocalDateTime getFileCreationTime() {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            // creation time or modified time;
            FileTime creationTime = attrs.creationTime();
            return LocalDateTime.ofInstant(
                    creationTime.toInstant(),
                    ZoneId.of("JST"));
        } catch (IOException e) {
            Log.e("UnitImage", "읽을 파일이 잘못되었습니다. ");
        }

        return null;
    }

    private boolean hasExifCreationTime() {
        ExifInterface exif = getExif();
        if (exif != null) {
            return exif.getAttribute(ExifInterface.TAG_DATETIME) != null;
        }

        return false;
    }

    private ExifInterface getExif() {
        try {
            ExifInterface exif = new ExifInterface(file.toPath().toString());
            return exif;
        } catch (IOException e) {
            Log.e("UnitImage", "읽을 파일이 잘못되었습니다. ");
            return null;
        }
    }
}


class ExifDatetimeExtractor {
    // 삼성: "yyyy:MM:dd HH:mm:ss"
    public static LocalDateTime getLocalDateTime(String exifDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(exifDateTime, formatter);

        return dateTime;
    }
}

class ExifLocationExtractor {
    public static double getLatitude(File file) {
        android.media.ExifInterface exif = null;
        try {
            exif = new android.media.ExifInterface(file.toPath().toString());
        } catch (IOException e) {
            Log.e("EXIF_LOCATION", "Latitude 파일 추출 IO 에러");
            return 0.0;
        }

        if (exif == null) {
            return 0.0;
        }

        String rawLat = exif.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE);
        String rawLatRef = exif.getAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE_REF);
        if ((rawLat != null) && (rawLatRef != null)) {
            if (rawLatRef.equals("N")) {
                return convertToDegree(rawLat);
            } else {
                return 0.0 - convertToDegree(rawLat);
            }
        } else {
            return 0.0;
        }
    }

    public static double getLongitude(File file) {
        android.media.ExifInterface exif = null;
        try {
            exif = new android.media.ExifInterface(file.toPath().toString());
        } catch (IOException e) {
            Log.e("EXIF_LOCATION", "Longitude 파일 추출 IO 에러");
            return 0.0;
        }

        if (exif == null) {
            return 0.0;
        }

        String rawLng = exif.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE);
        String rawLngRef = exif.getAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE_REF);
        if ((rawLng != null) && (rawLngRef != null)) {
            if (rawLngRef.equals("E")) {
                return convertToDegree(rawLng);
            } else {
                return 0.0 - convertToDegree(rawLng);
            }
        } else {
            return 0.0;
        }
    }


    public static int getLatitudeE6(File file) {
        return (int) (getLatitude(file) * 1000000);
    }

    public static int getLongitudeE6(File file) {
        return (int) (getLongitude(file) * 1000000);
    }

    private static double convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }

    /**
     * @param labels
     * @return objects to int
     * 0 : person / 1 : food / 2 : pet(dog or cat) / 3 : scenery
     * 4 : etc
     */
    public int categorizeObject(List<Label> labels) {
        // Smile, Fun, Eyelash, Bangs, Skin, Selfie, Hair
        // Food, Cuisine, Vegetable, Meal
        // Pet, Dog, Cat, Fur
        // Sky, Building, River, Rock, Lake, Mountain

        // Descending Order
        Collections.sort(labels, (label1, label2) -> {
            return label2.getText().compareTo(label1.getText());
        });

        float minConfidence = (float) 0.3;

        StringBuffer texts = new StringBuffer();
        for (Label label : labels) {
            if (label.getConfidence() < minConfidence)
                continue;
            texts.append(label.getText());
        }
        String text = texts.toString();

        if (text.contains("Smile") || text.contains("Fun") || text.contains("Eyelash")
                || text.contains("Bangs") || text.contains("Skin") || text.contains("Selfie")) {
            return 0;
        } else if (text.contains("Food") || text.contains("Cuisine")
                || text.contains("Vegetable") || text.contains("Meal")) {
            return 1;
        } else if (text.contains("Pet") || text.contains("Dog")
                || text.contains("Cat") || text.contains("Fur")) {
            return 2;
        } else if (text.contains("Sky") || text.contains("Building")
                || text.contains("River") || text.contains("Mountain")) {
            return 3;
        } else {
            return 4;
        }
    }

}