package com.company.Service;

import com.company.IConsumer;
import com.company.IExtract;
import com.company.IProducer;

import java.util.HashMap;

public class DataProcessService {
    public Object extract(IExtract iextract){
        iextract.extractData();
        return iextract.getExtractedData();
    }

    public void publishData(IProducer iProducer, Object o, String type){
        iProducer.produce(o, type);
    }

    public void fetchData(IConsumer consumer){
        consumer.consume();
    }
}
