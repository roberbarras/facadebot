package org.telegram.bot.facadebot.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.telegram.bot.facadebot.model.MessageReceived;
import org.telegram.bot.facadebot.model.MessageToSend;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${CLOUDKARAFKA_BROKERS}")
    private String brokers;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offset;

    @Value("${spring.kafka.properties.security.protocol}")
    private String protocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String mechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String credentials;

    public ConsumerFactory<String, MessageToSend> consumerFactory() {
        JsonDeserializer<MessageToSend> deserializer = new JsonDeserializer<>(MessageToSend.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offset);
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
        config.put(SaslConfigs.SASL_MECHANISM, mechanism);
        config.put(SaslConfigs.SASL_JAAS_CONFIG, credentials);


        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageToSend> customConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageToSend> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        return factory;
    }

    public ProducerFactory<String, MessageReceived> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
        config.put(SaslConfigs.SASL_MECHANISM, mechanism);
        config.put(SaslConfigs.SASL_JAAS_CONFIG, credentials);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, MessageReceived> customProducerFactory() {
        return new KafkaTemplate<>(producerFactory());
    }

}
