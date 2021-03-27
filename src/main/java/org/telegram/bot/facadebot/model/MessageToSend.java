package org.telegram.bot.facadebot.model;

import lombok.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MessageToSend implements Serializable {

    private String chatId;
    private String text;
    private String parseMode;
    private boolean disableWebPagePreview;

}
