package com.example.galleryproject.Model;

public class Label {
    String text;
    String entityId;
    Float confidence;

    public Label(String text, String entityId, Float confidence) {
        this.text = text;
        this.entityId = entityId;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "{ \"text\": \"" + text + "\", \"entityID\": \"" + entityId + "\", \"confidence\": " + confidence + " }";
    }

    public String getText() {
        return text;
    }

    public Float getConfidence() {
        return confidence;
    }
}