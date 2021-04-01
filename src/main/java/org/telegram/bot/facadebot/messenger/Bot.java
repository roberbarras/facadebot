package org.telegram.bot.facadebot.messenger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.bot.facadebot.model.MessageReceived;
import org.telegram.bot.facadebot.model.MessageToSend;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.admin}")
    private long admin;

    @Value("${cloudkarafka.topic.receivemessage}")
    private String receiveMessageTopic;

    private final KafkaTemplate<String, MessageReceived> customProducerMessage;

    Bot(KafkaTemplate<String, MessageReceived> kafkaTemplate) {
        this.customProducerMessage = kafkaTemplate;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("_____________________________________________________");
        log.info("| Update recived");
        log.info("| From: '{}'", update.getMessage().getFrom().toString());
        log.info("| Received message: '{}'", update.getMessage().getText());
        customProducerMessage.send(receiveMessageTopic, mapper(update));
    }

    public void sendMessage(MessageToSend message) {
        log.info("| Sent message: '{}'", message.getText().replace("\n", " "));
        log.info("_____________________________________________________\n");
        try {
            execute(mapper(message));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private MessageReceived mapper(Update update) {
        return MessageReceived.builder()
                .clientId(update.getMessage().getFrom().getId())
                .name(update.getMessage().getFrom().getFirstName())
                .chatId(update.getMessage().getChatId())
                .language(update.getMessage().getFrom().getLanguageCode())
                .text(update.getMessage().getText()).build();
    }

    private SendMessage mapper(MessageToSend message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text(message.getText())
                .parseMode(message.getParseMode())
                .disableWebPagePreview(message.isDisableWebPagePreview()).build();
    }
}
