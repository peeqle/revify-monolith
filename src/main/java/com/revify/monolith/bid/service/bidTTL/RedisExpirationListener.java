package com.revify.monolith.bid.service.bidTTL;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RedisExpirationListener {

    private final ReactiveRedisMessageListenerContainer listenerContainer;

    /**
     * ТУТ ОПАСНО, СТОИТ HARDCODE ДБ 0__ - ЧЕКАТЬ ПРИ РЕПЛИКАЦИИ ЧЕРЕЗ DC
     *
     * @param onExpire
     */
    public void listenToExpirationEvents(Consumer<String> onExpire) {
        Flux<String> messages = listenerContainer
                .receive(new ChannelTopic("__keyevent@0__:expired"))
                .map(ReactiveSubscription.Message::getMessage);
        messages.subscribe(onExpire);
    }
}
