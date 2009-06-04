package org.vtlabs.rtpproxy.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.vtlabs.rtpproxy.command.CommandTimeoutManager;
import org.vtlabs.rtpproxy.callback.CallbackHandler;
import org.vtlabs.rtpproxy.command.UpdateCommand;
import org.vtlabs.rtpproxy.udp.DatagramListener;
import org.vtlabs.rtpproxy.udp.DatagramService;

/**
 * RTPProxy client API facade class.
 *
 * @author Marcos Hack <marcosh@voicetechnology.com.br>
 */
public class RTPProxyClient {

    private CommandTimeoutManager commandTimeout;
    private DatagramService udpService;
    private CallbackHandler callbackHandler;
    private ScheduledThreadPoolExecutor executor;
    private RTPProxyClientConfigurator config;
    private List<RTPProxyServer> serverList;

    public RTPProxyClient() throws IOException, ConfigErrorException {
        config = createClientConfigurator();
        executor = createThreadPoolExecutor(config.getScheduledThreadPoolSize());
        commandTimeout = createCommandTimeoutManager(executor);
        callbackHandler = createCallbackHandler(commandTimeout);
        udpService = createDatagraService(config.getBindPort(), callbackHandler);
        serverList = config.getServerList();
    }

    /**
     * Asynchronously create a new RTPProxy session filled only with the Callee
     * media address.
     * 
     * To create the Caller media address of an existing session use the method
     * updateSession().
     *
     * @param String to be used as session ID.
     * @param Application data object, will be passed as argument in the
     *        callback method.
     * @param Listener that will receive the callback event.
     * @see RTPProxyClientListener
     */
    public void createSession(String sessionID, Object appData,
            RTPProxyClientListener listener) throws NoServerAvailableException {
        UpdateCommand updateCmd = new UpdateCommand();
        updateCmd.setCallID(sessionID);

        // No matter the fromtag content since it matchs that used in the
        // updateSession() method below to link the callee and caller session in
        // the RTPProxy.
        updateCmd.setFromTag("fromtag");

        String cookie = updateCmd.getCookie();
        String message = updateCmd.getMessage();
        InetSocketAddress serverAddr = getServer().getAddress();
        udpService.send(cookie, message, serverAddr);
    }

    /**
     * Asynchronously create the Caller media address of an existing session.
     *
     * @param session to be updated
     * @param Application data object, will be passed as argument in the
     *        callback method.
     * @param Listener that will receive the callback event.
     */
    public void updateSession(RTPProxySession session, Object appData,
            RTPProxyClientListener listener) throws NoServerAvailableException {
        UpdateCommand updateCmd = new UpdateCommand(session);

        // No matter the fromtag and totag content since it matchs that used in
        // the createSession() method above to link the callee and caller
        // session in the RTPProxy.
        updateCmd.setFromTag("totag");
        updateCmd.setToTag("fromtag");

        String cookie = updateCmd.getCookie();
        String message = updateCmd.getMessage();
        InetSocketAddress serverAddr = session.getServer().getAddress();
        udpService.send(cookie, message, serverAddr);
    }

    /**
     * Asynchronously destroy the given RTPProxySession releasing all resources
     * in the RTPProxy server.
     *
     * @param Session to be destroyed.
     */
    public void destroySession(RTPProxySession session, Object appData,
            RTPProxyClientListener listener) throws NoServerAvailableException {
    }

    /**
     * Get the next avaiable RTPProxy server to be used.
     *
     * @return
     * @throws org.vtlabs.rtpproxy.client.NoServerAvailableException if there
     *         aren't servers available.
     */
    protected RTPProxyServer getServer() throws NoServerAvailableException {
        // TODO [marcoshack] RTPProxy servers load balance algorithm
        if (serverList.size() > 0) {
            return serverList.get(0);
        } else {
            throw new NoServerAvailableException("No RTPProxy server available");
        }
    }
    
    /**
     * Factory method to create CommandManager.
     *
     * @return
     */
    protected CommandTimeoutManager createCommandTimeoutManager(
            ScheduledThreadPoolExecutor executor) {
        return new CommandTimeoutManager(executor);
    }

    /**
     * Factory method to create DatagramService.
     *
     * @return
     */
    protected DatagramService createDatagraService(int bindPort,
            DatagramListener listener) throws IOException {
        return new DatagramService(bindPort, listener);
    }
    
    /**
     * Factory method to create RTPClientResponseHandler.
     *
     * @return
     */
    protected CallbackHandler createCallbackHandler(
            CommandTimeoutManager commandManager) {
        return new CallbackHandler(commandManager);
    }

    /**
     * Factory method to create ScheduledThreadPoolExecutor.
     *
     * @return
     */
    protected ScheduledThreadPoolExecutor createThreadPoolExecutor(
            int poolSize) {
        return new ScheduledThreadPoolExecutor(poolSize);
    }

    /**
     * Factory method to create RTPProxyClientConfigurator.
     *
     * @return
     */
    protected RTPProxyClientConfigurator createClientConfigurator() 
            throws ConfigErrorException {
        return new RTPProxyClientConfigurator();
    }
}