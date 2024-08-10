package com.sascom.stockpricebackend.application.kis.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.stockpricebackend.application.kis.model.ResolvedData;
import com.sascom.stockpricebackend.application.kis.properties.PublishDest;
import com.sascom.stockpricebackend.application.kis.properties.TrName;
import com.sascom.stockpricebackend.application.kis.util.OpsDataParser;
import com.sascom.stockpricebackend.global.event.ChickenStockEventPublisher;
import com.sascom.stockpricebackend.application.kis.properties.KisAccessProperties;
import com.sascom.stockpricebackend.global.redis.pub.RedisMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KisWebSocketHandler extends TextWebSocketHandler {

    private final OpsDataParser opsDataParser;
    private final KisAccessProperties kisAccessProperties;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisMessagePublisher redisMessagePublisher;
    private final ObjectMapper objectMapper;
    private final ChickenStockEventPublisher eventPublisher;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedPayload = message.getPayload();
        log.info("[RECEIVE] : {}", receivedPayload);

        ResolvedData<?> resolvedData = opsDataParser.resolveMessage(receivedPayload);

        if (OpsDataParser.PINGPONG_TR_ID.equals(resolvedData.trId())) {
            log.info("[SEND] : {}", receivedPayload);
            session.sendMessage(new TextMessage(receivedPayload));
            return;
        }
        if (resolvedData.data() != null) {
            log.info("resolvedData: {}", resolvedData.data());

            String messageTrId = resolvedData.trId();

            // TODO 커스텀 에러로 수정
            String dest = getDest(messageTrId)
                    .orElseThrow(() -> new IllegalArgumentException("알수없는 경로입니다."));

            String sendPayload = objectMapper.writeValueAsString(resolvedData.data());
            messagingTemplate.convertAndSend(dest, sendPayload);
            if (dest.equals(PublishDest.REALTIME_PURCHASE.getDest())) {
                redisMessagePublisher.publish(PublishDest.REALTIME_PURCHASE.getDest(), sendPayload);
            }
        }
    }

    private Optional<String> getDest(String messageTrId) {
        String hokaTrId = kisAccessProperties.getTrIdMap().get(TrName.REALTIME_HOKA.name());
        if (hokaTrId.equals(messageTrId)) {
            return Optional.of(PublishDest.REALTIME_HOKA.getDest());
        }

        String purchaseTrId = kisAccessProperties.getTrIdMap().get(TrName.REALTIME_PURCHASE.name());
        if (purchaseTrId.equals(messageTrId)) {
            return Optional.of(PublishDest.REALTIME_PURCHASE.getDest());
        }

        return Optional.empty();
    }
  
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("kis session connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (session.isOpen()) {
            session.close();
        }

        log.warn("kis session closed: {}", session.getId());
        log.warn("close status: {}", status);

        eventPublisher.publishDisconnectEvent(UUID.randomUUID().toString());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("kis session error: {}", exception.getMessage());
        super.handleTransportError(session, exception);
    }
}
