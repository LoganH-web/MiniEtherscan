package com.example.minieetherscan.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.example.minieetherscan.service.EthereumService;

@Component
public class StartupListener {

    private final EthereumService ethereumService;

    public StartupListener(EthereumService ethereumService) {
        this.ethereumService = ethereumService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        ethereumService.logBlockchainInfo();
        ethereumService.subscribeToNewBlocks();
    }
}
