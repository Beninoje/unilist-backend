package com.unilist.campora.controllers;

import com.unilist.campora.dto.messages.MessageResponseDto;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.MessageRepository;
import com.unilist.campora.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/get/unread")
    public ResponseEntity<?> getTotalUnreadMessage(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));
        return ResponseEntity.ok(messageRepository.getUnreadCountsPerChat(currentUser.getId()));
    }

}
