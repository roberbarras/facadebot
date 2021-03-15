package org.telegram.bot.facadebot.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.bot.facadebot.messenger.Bot;
import org.telegram.bot.facadebot.model.TelegramMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class KafkaTestListener {

    @Autowired
    Bot bot;

    @KafkaListener(topics = "org.telegram.bot.sendmessage", groupId = "group_json",
            containerFactory = "customConsumerFactory")
    public void consumeJson(TelegramMessage message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(message.getText())
                .parseMode(message.getParseMode())
                .disableWebPagePreview(message.isDisableWebPagePreview()).build();
        bot.sendMessage(sendMessage);
    }
}

