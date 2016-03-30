package com.ai.baas.amc.topology.core.util;

import java.util.Map;
import java.util.Properties;

import com.ai.baas.amc.test.ProducerProxy;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProxy {
	private static String kafka_name = null;
	private static Producer<String, String> producer = null;
	private static KafkaProxy instance = null;
	
	public static KafkaProxy getInstance(Map<String,String> conf){
        if(instance == null){
            synchronized(ProducerProxy.class){
                if(instance == null){
                    instance = new KafkaProxy();
                    loadResources(conf);
                }
            }
        }
        return instance;
    }
	private static void loadResources(Map<String,String> conf){
		Properties props = new Properties();
		props.put(AmcConstants.KafkaConfig.BROKER_LIST, conf.get(AmcConstants.KafkaConfig.BROKER_LIST));
        props.put(AmcConstants.KafkaConfig.SERIALIZER_CLASS, conf.get(AmcConstants.KafkaConfig.SERIALIZER_CLASS)); 
        props.put(AmcConstants.KafkaConfig.REQUIRED_ACKS, conf.get(AmcConstants.KafkaConfig.REQUIRED_ACKS));
        producer = new Producer<String, String>(new ProducerConfig(props));
        kafka_name = conf.get(AmcConstants.KafkaConfig.KAFKA_TOPIC_XK);
	}
	
	public void sendMessage(String message){
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(kafka_name, message);
		producer.send(data);
	}
	
	public void close(){
		if(producer != null){
			producer.close();
		}
	}
}
