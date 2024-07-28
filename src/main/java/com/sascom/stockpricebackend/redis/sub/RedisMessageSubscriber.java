package com.sascom.stockpricebackend.redis.sub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class RedisMessageSubscriber implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String from = new String(pattern);
        String messageBody = new String(message.getBody());
        System.out.println(Arrays.toString(message.getBody()));
        log.info("[Redis Received] from: {}", from);

        // TODO 문자열에 쓰래기값 제거. (바이트 배열 7번 인덱스까지 쓰레기값)
        log.info("[Redis Received] message: {}", messageBody);
    }
}
