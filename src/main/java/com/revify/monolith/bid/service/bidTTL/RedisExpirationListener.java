package com.revify.monolith.bid.service.bidTTL;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RedisExpirationListener {

    private final RedisMessageListenerContainer listenerContainer;

    /**
     * ТУТ ОПАСНО, СТОИТ HARDCODE ДБ 0__ - ЧЕКАТЬ ПРИ РЕПЛИКАЦИИ ЧЕРЕЗ DC
     *
     * @param onExpire
     */
    public void listenToExpirationEvents(Consumer<Message> onExpire) {
        listenerContainer.addMessageListener((message, pattern) -> {
            onExpire.accept(message);
        }, new ChannelTopic("__keyevent@0__:expired"));
    }
}
