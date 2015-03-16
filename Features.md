Features available in current stable version of **rtpproxy-client-api**:

## Operations ##
  * **create**: Create a new session containing the the callee's media address. It supports  caller's address pre-filling;
  * **update**: Update a existing session to create the caller's media address. It supports  callee's address pre-filling;
  * **destroy**: Delete a existent in the RTPPRoxy;

## Server scheduling ##
  * **static**: Always use the first server of the configured server list.
  * **round-robin**: Equally balance the requests for all configured servers.