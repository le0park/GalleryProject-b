package com.example.galleryproject.Model;

public class Label {
    protected String text;
    protected String entityId;
    protected Float confidence;

    public Label() {
        this.text = new String();
        this.entityId = new String();
        this.confidence = -1.0f;
    }

    public Label(String text, String entityId, Float confidence) {
        this.text = text;
        this.entityId = entityId;
        this.confidence = confidence;
    }

    public String getText() {
        return this.text;
    }

    public String getEntityId() {
        return this.entityId;
    }
    public Float getConfidence() {
        return this.confidence;
    }

    @Override
    public String toString() {
        return "{ \"text\": \"" + text + "\", \"entityID\": \"" + entityId + "\", \"confidence\": " + confidence + " }";
    }
}