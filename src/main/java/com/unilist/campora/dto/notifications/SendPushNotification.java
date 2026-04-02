package com.unilist.campora.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendPushNotification{
        String pushToken;
        String senderFirstName;
        String content;

}
