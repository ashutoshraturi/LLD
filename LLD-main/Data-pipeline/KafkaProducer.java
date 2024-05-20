package com.company.impl;

import com.company.IProducer;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducer<K, V> implements IProducer {


    @Override
    public void produce(Object concreteObject, Object type) {
        if(type == "hashMap"){
            try (KafkaProducer<K, V> producer = new KafkaProducer<K, V>()) {
                    ProducerRecord<K, V> producerRecord = new ProducerRecord<>("topic-1", K, V);
                    try {
                        producer.send(producerRecord);
                    } catch (SerializationException e)
                    {
                    } finally {
                        producer.flush();
                    }
                }
            }
        }
    }
