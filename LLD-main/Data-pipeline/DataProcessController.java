package com.company.Controller;

import com.company.IConsumer;
import com.company.IExtract;
import com.company.IProducer;
import com.company.Service.DataProcessService;
import com.company.impl.KafkaConsumer;
import com.company.impl.FileDataExtractor;
import com.company.impl.KafkaProducer;

public class DataProcessController {
    IConsumer iconsumer = new KafkaConsumer();
    IExtract iextract = new FileDataExtractor<>();
    IProducer iproducer = new KafkaProducer<>();


    public void initiateProcess(){
        DataProcessService dataProcessService = new DataProcessService();
        Object extractedData = dataProcessService.extract(iextract);
        dataProcessService.publishData(iproducer, extractedData, "hashap");
    }
}
