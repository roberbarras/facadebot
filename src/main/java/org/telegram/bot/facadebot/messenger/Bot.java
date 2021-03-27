package org.telegram.bot.facadebot.messenger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.telegram.bot.facadebot.model.MessageReceived;
import org.telegram.bot.facadebot.model.MessageToSend;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private KafkaTemplate<String, MessageReceived> customProducerMessage;

    public void sendMessage(MessageToSend message) {
        try {
            execute(mapper(message));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
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
        Optional.of(update.getMessage().getChatId())
                .filter(elem -> elem.equals(-570179047L))
                .ifPresentOrElse((elem) -> {
                    customProducerMessage.send("org.telegram.bot.receiveadminmessage", mapper(update));
                }, () -> {
                    customProducerMessage.send("org.telegram.bot.receivemessage", mapper(update));
                });
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
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(message.getText())
                .parseMode(message.getParseMode())
                .disableWebPagePreview(message.isDisableWebPagePreview()).build();
        return sendMessage;
    }
}
