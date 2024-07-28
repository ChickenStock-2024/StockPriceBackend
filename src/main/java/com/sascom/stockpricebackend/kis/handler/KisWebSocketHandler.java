package com.sascom.stockpricebackend.kis.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.stockpricebackend.kis.model.ResolvedData;
import com.sascom.stockpricebackend.kis.properties.KisAccessProperties;
import com.sascom.stockpricebackend.kis.properties.PublishDest;
import com.sascom.stockpricebackend.kis.properties.StockName;
import com.sascom.stockpricebackend.kis.properties.TrName;
import com.sascom.stockpricebackend.kis.util.KisWebSocketUtil;
import com.sascom.stockpricebackend.kis.util.OpsDataParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

import static com.sascom.stockpricebackend.kis.util.OpsDataParser.PINGPONG_TR_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KisWebSocketHandler extends TextWebSocketHandler {

    private final OpsDataParser opsDataParser;
    private final KisWebSocketUtil kisWebSocketUtil;
    private final KisAccessProperties kisAccessProperties;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String receivedPayload = message.getPayload();
        log.info("[RECEIVE] : {}", receivedPayload);

        ResolvedData<?> resolvedData = opsDataParser.resolveMessage(receivedPayload);

        if (PINGPONG_TR_ID.equals(resolvedData.trId())) {
            log.info("[SEND] : {}", receivedPayload);
            session.sendMessage(new TextMessage(receivedPayload));

            // TODO 전송 확인을 위한 임시 송신
            messagingTemplate.convertAndSend("/stock-hoka", receivedPayload);
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
        }
    }

    private Optional<String> getDest(String messageTrId) {
        String hokaTrId = kisAccessProperties.getTrIdMap().get(TrName.REALTIME_HOKA.name());
        if (hokaTrId.equals(messageTrId)) {
            return Optional.of(PublishDest.REALTIME_HOKA.getDest());
        }

        String purchaseTrId = kisAccessProperties.getTrIdMap().get(TrName.REALTIME_HOKA.name());
        if (purchaseTrId.equals(messageTrId)) {
            return Optional.of(PublishDest.REALTIME_PURCHASE.getDest());
        }

        return Optional.empty();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("kis session connected: {}", session.getId());
        kisWebSocketUtil.subscribe(session, TrName.REALTIME_HOKA, StockName.SAMSUNG);
        kisWebSocketUtil.subscribe(session, TrName.REALTIME_PURCHASE, StockName.SAMSUNG);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (session.isOpen()) {
            session.close();
        }

        log.warn("kis session closed: {}", session.getId());
        log.warn("close status: {}", status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("kis session error: {}", exception.getMessage());
        super.handleTransportError(session, exception);
    }
}
