package com.projecthub.base.sync.application.service;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

@Component
public class NetworkStatusChecker {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStatusChecker.class);

    @Value("${sync.remote.host:localhost}")
    private String remoteHost;

    @Value("${sync.remote.port:5432}")
    private int remotePort;

    @Value("${sync.network.timeout:5000}")
    private int timeout;

    public boolean isNetworkAvailable() {
        // First check if basic internet connectivity is available
        if (!checkInternetConnectivity()) {
            logger.warn("No internet connectivity available");
            return false;
        }

        // Then check if remote server is reachable
        return checkRemoteServerConnection();
    }

    private boolean checkInternetConnectivity() {
        try {
            InetAddress[] addresses = InetAddress.getAllByName("8.8.8.8");
            return addresses.length > 0 && addresses[0].isReachable(timeout);
        } catch (IOException e) {
            logger.debug("Internet connectivity check failed", e);
            return false;
        }
    }

    private boolean checkRemoteServerConnection() {
        TelnetClient telnet = new TelnetClient();
        telnet.setConnectTimeout(timeout);

        try {
            telnet.connect(remoteHost, remotePort);
            telnet.disconnect();
            return true;
        } catch (IOException e) {
            logger.debug("Remote server connection check failed", e);
            return false;
        }
    }
}
