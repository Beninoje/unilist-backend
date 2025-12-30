package com.unilist.unilist.services.chat;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.Message;
import com.unilist.unilist.repository.MessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

}
