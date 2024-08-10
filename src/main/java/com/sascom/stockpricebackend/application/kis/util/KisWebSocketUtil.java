package com.sascom.stockpricebackend.application.kis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.stockpricebackend.application.kis.properties.*;
import com.sascom.stockpricebackend.kis.properties.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KisWebSocketUtil {

    private final KisUserProperties userProperties;
    private final ObjectMapper objectMapper;
    private final KisAccessProperties accessProperties;

    public KisWebSocketUtil(
            @Qualifier("kis.blue-com.sascom.stockpricebackend.kis.properties.KisBlueProperties")
            KisUserProperties userProperties,
            ObjectMapper objectMapper,
            KisAccessProperties accessProperties
    )
    {
        this.userProperties = userProperties;
        this.objectMapper = objectMapper;
        this.accessProperties = accessProperties;
    }

    public void subscribe(WebSocketSession session, TrName trName, StockName stockName) throws IOException {
        String requestMessage = createRequestMessage(TrType.SUBSCRIBE, trName, stockName);
        sendMessage(session, requestMessage);
    }
    public void cancel(WebSocketSession session, TrName trName, StockName stockName) throws IOException {
        String requestMessage = createRequestMessage(TrType.CANCEL, trName, stockName);
        sendMessage(session, requestMessage);
    }

    private void sendMessage(WebSocketSession session, String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            log.error("Kis WebSocket Session is not open.");
        }
    }

    private String createRequestMessage(TrType trType, TrName trName, StockName stockName) throws IOException {
        HashMap<String, Object> requestMessageInfo = getRequestMap(trType, trName, stockName);
        return objectMapper.writeValueAsString(requestMessageInfo);
    }

    private HashMap<String, Object> getRequestMap(TrType trType, TrName trName, StockName stock) throws JsonProcessingException {
        Map<String, Object> header = userProperties.getHeader(trType.getValue());

        HashMap<String, Object> body = new HashMap<>();
        body.put("tr_id", accessProperties.getTrIdMap().get(trName.name()));
        body.put("tr_key", accessProperties.getStockCodeMap().get(stock.getName()));
        HashMap<String, Object> bodyWrapper = new HashMap<>();
        bodyWrapper.put("input", body);

        HashMap<String, Object> data = new HashMap<>();
        data.put("header", header);
        data.put("body", bodyWrapper);

        return data;
    }
}
