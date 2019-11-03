package com.example.galleryproject.Model.dbscan;

import java.util.List;

public interface Algorithm {
    void setPoints(List points);

    void cluster();
}