package com.valcare.parkinglot.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "codeDecode_Topic",groupId = "readGroup")
    public void listenToTopic(String readMessage){
        System.out.println("The message is received "+readMessage);
    }

}
