package com.unilist.campora.dto.ws;

import java.util.Map;

public record UnreadCountDto(Map<String,Integer> unreadPerChat, int total) {
}
