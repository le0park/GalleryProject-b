package com.example.galleryproject.Model;

public class LabelGroupWithImage {
    private Image image;
    private LabelGroup labelGroup;

    public LabelGroupWithImage(LabelGroup labelGroup, Image image) {
        this.labelGroup = labelGroup;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public LabelGroup getLabelGroup() {
        return labelGroup;
    }

    public boolean isOfImage(Image im) {
        return image.equals(im);
    }
}
