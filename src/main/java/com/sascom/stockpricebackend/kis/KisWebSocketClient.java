package com.sascom.stockpricebackend.kis;

import com.sascom.stockpricebackend.kis.properties.KisAccessProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class KisWebSocketClient extends StandardWebSocketClient {

    private final WebSocketHandler kisWebSocketHandler;
    private final KisAccessProperties accessProperties;

    @EventListener(ApplicationReadyEvent.class)
    protected void initializeConnection() {
        String kisApiUri = accessProperties.getUrl() + ":" + accessProperties.getReal_port();
        connectWebSocket(kisApiUri);
    }

    private void connectWebSocket(String apiUri) {

        CompletableFuture<WebSocketSession> execute = execute(kisWebSocketHandler, apiUri);
        execute.thenAccept(webSocketSession -> {
            log.info("kis session id: {}", webSocketSession.getId());
        });
    }
}
