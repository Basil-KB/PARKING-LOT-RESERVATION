package com.valcare.parkinglot.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    ProducerService producer;

    @GetMapping("/producermsg")
    public void getMessageFromProducer(@RequestParam("message")String message){
        producer.sendMsgTopic(message);

    }
}
