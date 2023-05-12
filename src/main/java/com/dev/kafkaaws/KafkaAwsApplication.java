package com.dev.kafkaaws;

import com.dev.kafkaaws.models.Devs4jTransactions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
@EnableScheduling()
public class KafkaAwsApplication {

	private static final Logger log = LoggerFactory.getLogger(KafkaAwsApplication.class);
	@Autowired
	KafkaTemplate<String,String> kafkaTemplate;

	@Autowired
	private ObjectMapper mapper;

	public static void main(String[] args) {
		SpringApplication.run(KafkaAwsApplication.class, args);
	}
@KafkaListener(topics = "devs4j-transactions", groupId = "devs4jGroup", containerFactory = "kafkaListenerContainerFactory")
	public void listen(List<ConsumerRecord<String,String>> messages) throws JsonProcessingException {
	for(ConsumerRecord message: messages){
		//Devs4jTransactions devs4jTransactions = mapper.readValue(message.value().toString(), Devs4jTransactions.class);
		log.info("Partition: {} Offset: {} message: {}", message.partition(),message.offset(),message.value());
	}
	}

	@Scheduled(fixedRate = 10000)
	public void sendMessages() throws JsonProcessingException {

		Faker faker = new Faker();
		for(int i=0;i<10000;i++){
			Devs4jTransactions devs4jTransactions = new Devs4jTransactions();
			devs4jTransactions.setUserName(faker.name().username());
			devs4jTransactions.setNombre(faker.name().firstName());
			devs4jTransactions.setApellido(faker.name().lastName());
			devs4jTransactions.setMonto(faker.number().randomDouble(3,0,20000000));
			kafkaTemplate.send("devs4j-transactions", devs4jTransactions.getUserName(),mapper.writeValueAsString(devs4jTransactions));
		}
	}

}
