package com.sascom.stockpricebackend.kis.handler;

import com.sascom.stockpricebackend.kis.model.ResolvedData;
import com.sascom.stockpricebackend.kis.properties.StockName;
import com.sascom.stockpricebackend.kis.properties.TrName;
import com.sascom.stockpricebackend.kis.util.KisWebSocketUtil;
import com.sascom.stockpricebackend.kis.util.OpsDataParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import static com.sascom.stockpricebackend.kis.util.OpsDataParser.PINGPONG_TR_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class KisWebSocketHandler extends TextWebSocketHandler {

    private final KisWebSocketUtil kisWebSocketUtil;
    private final OpsDataParser opsDataParser;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("[RECEIVE] : {}", payload);

        ResolvedData<?> resolvedData = opsDataParser.resolveMessage(payload);

        if (PINGPONG_TR_ID.equals(resolvedData.trId())) {
            log.info("[SEND] : {}", payload);
            session.sendMessage(new TextMessage(payload));
            return;
        }
        if (resolvedData.data() != null) {
            log.info("resolvedData: {}", resolvedData.data());
        }
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
