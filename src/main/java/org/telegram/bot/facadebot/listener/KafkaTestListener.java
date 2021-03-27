package org.telegram.bot.facadebot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.bot.facadebot.messenger.Bot;
import org.telegram.bot.facadebot.model.MessageToSend;

@Service
public class KafkaTestListener {

    @Autowired
    Bot bot;

    @KafkaListener(topics = "org.telegram.bot.sendmessage", containerFactory = "customConsumerFactory")
    public void consumeJson(MessageToSend message) {
        bot.sendMessage(message);
    }

    @KafkaListener(topics = "org.telegram.bot.sendadminmessage", containerFactory = "customConsumerFactory")
    public void consumeAdminJson(MessageToSend message) {
        bot.sendMessage(message);
    }
}

