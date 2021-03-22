package org.telegram.bot.facadebot.messenger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private KafkaTemplate<String, Update> customProducerMessage;

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "ZaraBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAMTOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        customProducerMessage.send("org.telegram.bot.receivemessage", update);
    }
}
