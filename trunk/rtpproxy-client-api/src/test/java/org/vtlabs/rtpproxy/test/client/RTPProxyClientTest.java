/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vtlabs.rtpproxy.test.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.junit.Before;
import org.junit.Test;
import org.vtlabs.rtpproxy.client.RTPProxyClient;
import org.vtlabs.rtpproxy.client.RTPProxyClientConfig;
import org.vtlabs.rtpproxy.client.RTPProxyClientConfigException;
import org.vtlabs.rtpproxy.client.RTPProxyClientConfigurator;
import org.vtlabs.rtpproxy.client.RTPProxyClientListener;
import org.vtlabs.rtpproxy.client.RTPProxyServer;
import org.vtlabs.rtpproxy.client.RTPProxySession;
import org.vtlabs.rtpproxy.command.Command;
import org.vtlabs.rtpproxy.command.CommandListener;
import org.vtlabs.rtpproxy.command.CommandTimeoutManager;
import org.vtlabs.rtpproxy.command.UpdateCommand;
import org.vtlabs.rtpproxy.mock.client.RTPProxyClientListenerMOCK;
import org.vtlabs.rtpproxy.mock.command.CommandTimeoutManagerMOCK;
import org.vtlabs.rtpproxy.mock.udp.DatagramServiceMOCK;
import org.vtlabs.rtpproxy.udp.DatagramListener;
import org.vtlabs.rtpproxy.udp.DatagramService;
import static org.junit.Assert.*;

/**
 *
 * @author mhack
 */
public class RTPProxyClientTest {

    RTPProxyClientMOCK client;
    private RTPProxyClientConfig clientConfig;
    private RTPProxyClientListener listener;

    @Before
    public void init() throws Exception {
        clientConfig = RTPProxyClientConfigurator.load("127.0.0.1:222");
        client = new RTPProxyClientMOCK(clientConfig);
        listener = new RTPProxyClientListenerMOCK();
    }

    @Test
    public void createSession() throws Exception {
        String sessionID = "create_session_id";
        Object appData = new Object();
        String expectedSentMessage = "U " + sessionID + " 0 0 fromtag 0";

        client.createSession(sessionID, appData, listener);

        // Asserts
        CommandTimeoutManagerMOCK timeout = client.getCommandTimeoutManager();
        UpdateCommand command = (UpdateCommand) timeout.pendingCommand;
        assertNotNull("Update command wasn't added to timeout manager",
                command);

        DatagramServiceMOCK udpService = client.getDatagramService();
        assertEquals("Invalid sent message", expectedSentMessage,
                udpService.sentMessage);
    }

    @Test
    public void updateSession() throws Exception {
        String sessionID = "update_session_id";
        Object appData = new Object();
        RTPProxyServer server = new RTPProxyServer();
        server.setAddress(new InetSocketAddress("127.0.0.1", 22222));
        RTPProxySession session = new RTPProxySession();
        session.setSessionID(sessionID);
        session.setServer(server);
        String expectedSentMessage = "U " + sessionID + " 0 0 totag fromtag";

        client.updateSession(session, appData, listener);

        // Asserts
        CommandTimeoutManagerMOCK timeout = client.getCommandTimeoutManager();
        UpdateCommand command = (UpdateCommand) timeout.pendingCommand;
        assertNotNull("Update command wasn't added to timeout manager",
                command);

        DatagramServiceMOCK udpService = client.getDatagramService();
        assertEquals("Invalid sent message", expectedSentMessage,
                udpService.sentMessage);
    }

    protected class RTPProxyClientMOCK extends RTPProxyClient {

        public RTPProxyClientMOCK(RTPProxyClientConfig config)
                throws IOException, RTPProxyClientConfigException {
            super(config);
        }

        public DatagramServiceMOCK getDatagramService() {
            return (DatagramServiceMOCK) udpService;
        }

        public CommandTimeoutManagerMOCK getCommandTimeoutManager() {
            return (CommandTimeoutManagerMOCK) commandTimeout;
        }

        @Override
        protected CommandTimeoutManager createCommandTimeoutManager(
                ScheduledThreadPoolExecutor executor, long commandTimeout) {

            return new CommandTimeoutManagerMOCK(executor, commandTimeout);
        }

        @Override
        protected DatagramService createDatagraService(
                int bindPort, DatagramListener listener) throws IOException {

            return new DatagramServiceMOCK(bindPort, listener);
        }

        @Override
        protected ScheduledThreadPoolExecutor createThreadPoolExecutor(
                int poolSize) {
            return null;
        }
    }
}
