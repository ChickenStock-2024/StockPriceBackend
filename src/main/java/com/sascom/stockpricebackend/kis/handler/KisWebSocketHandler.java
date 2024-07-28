package com.sascom.stockpricebackend.kis.handler;

import com.sascom.stockpricebackend.kis.properties.StockName;
import com.sascom.stockpricebackend.kis.properties.TrName;
import com.sascom.stockpricebackend.kis.util.KisWebSocketUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class KisWebSocketHandler extends TextWebSocketHandler {

    private final KisWebSocketUtil kisWebSocketUtil;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("kis session connected: {}", session.getId());
        kisWebSocketUtil.subscribe(session, TrName.HOKA, StockName.SAMSUNG);
        kisWebSocketUtil.subscribe(session, TrName.PURCHASE, StockName.SAMSUNG);

        session.close();
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
