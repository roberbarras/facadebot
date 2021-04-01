package org.telegram.bot.facadebot.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.bot.facadebot.messenger.Bot;
import org.telegram.bot.facadebot.model.MessageToSend;

@Service
public class KafkaTestListener {

    private final Bot bot;

    public KafkaTestListener(Bot bot) {
        this.bot = bot;
    }

    @KafkaListener(topics = "${cloudkarafka.topic.sendmessage}", containerFactory = "customConsumerFactory")
    public void consumeJson(MessageToSend message) {
        bot.sendMessage(message);
    }
}

