package com.projecthub.base.sync.application.service;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Component
public class NetworkStatusChecker {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStatusChecker.class);

    @Value("${sync.remote.host:localhost}")
    private String remoteHost;

    @Value("${sync.remote.port:5432}")
    private int remotePort;

    @Value("${sync.network.timeout:5000}")
    private int timeout;

    public NetworkStatusChecker() {
    }

    public boolean isNetworkAvailable() {
        // First check if basic internet connectivity is available
        if (!this.checkInternetConnectivity()) {
            NetworkStatusChecker.logger.warn("No internet connectivity available");
            return false;
        }

        // Then check if remote server is reachable
        return this.checkRemoteServerConnection();
    }

    private boolean checkInternetConnectivity() {
        try {
            final InetAddress[] addresses = InetAddress.getAllByName("8.8.8.8");
            return 0 < addresses.length && addresses[0].isReachable(this.timeout);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            NetworkStatusChecker.logger.debug("Internet connectivity check failed", e);
            return false;
        }
    }

    private boolean checkRemoteServerConnection() {
        final TelnetClient telnet = new TelnetClient();
        telnet.setConnectTimeout(this.timeout);

        try {
            telnet.connect(this.remoteHost, this.remotePort);
            telnet.disconnect();
            return true;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            NetworkStatusChecker.logger.debug("Remote server connection check failed", e);
            return false;
        }
    }
}
