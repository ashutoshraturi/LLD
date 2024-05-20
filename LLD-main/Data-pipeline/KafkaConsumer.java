package com.company.impl;

import com.company.IConsumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

public class KafkaConsumer implements IConsumer {
    @Override
    public void consume() {
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(new File(new String()))) {

            try (Consumer<Object, Object> consumer = new KafkaConsumer<>(new Properties()); consumer) {
                consumer.subscribe(Collections.singletonList("Topic-1"));
                while (true) {
                    for (ConsumerRecord<Object, Object> canonicalrecord : consumerRecords) {
                        buffWriter.write(canonicalrecord.value() + ",");
                    }
                }
        }
    }
    catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
}
