package com.unilist.campora.services.chat;

import com.unilist.campora.dto.messages.MessageResponseDto;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Message;
import com.unilist.campora.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }



    public List<MessageResponseDto> getAllMessagesByChat(Chat chat){
        return chat.getMessages().stream()
                .filter(distinctByKey(Message::getId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(msg -> MessageResponseDto.builder()
                        .messageId(msg.getId())
                        .senderId(msg.getSender().getId())
                        .senderFirstName(msg.getSender().getFirstName())
                        .senderLastName(msg.getSender().getLastName())
                        .replyToMessageId(
                                msg.getReplyTo() != null ? msg.getReplyTo().getId() : null
                        )
                        .replyToContent(
                                msg.getReplyTo() != null ? msg.getReplyTo().getContent() : null
                        )
                        .replyToSenderFirstName(
                                msg.getReplyTo() != null ? msg.getReplyTo().getSender().getFirstName() : null
                        )
                        .replyToSenderLastName(
                                msg.getReplyTo() != null ? msg.getReplyTo().getSender().getLastName() : null
                        )
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build()
                ).toList();
    }

    private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
        java.util.Set<Object> seen = new java.util.concurrent.ConcurrentSkipListSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
