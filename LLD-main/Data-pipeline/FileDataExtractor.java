package com.company.impl;

import com.company.IExtract;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import static javafx.scene.input.KeyCode.T;

public class FileDataExtractor<K, V> implements IExtract {

    private File file = new File(new String());
    public HashMap<K, V> extractedData =  new HashMap<K, V>();

    @Override
    public void extractData() {
        //extract file data
        //update transformedFile
    }

    public HashMap<K, V> getExtractedData() {
        return extractedData;
    }

}
