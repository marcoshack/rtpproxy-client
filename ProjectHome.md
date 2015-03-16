Java client API to create and control [RTPProxy](http://www.rtpproxy.org/) server sessions.

Features available in the current version:

  * **Operations**
    * **create**: Create a new session containing the the callee's media address. It supports  caller's address pre-filling;
    * **update**: Update a existing session to create the caller's media address. It supports  callee's address pre-filling;
    * **destroy**: Delete a existent in the RTPPRoxy;

  * **Server scheduling**
    * **static**: Always use the first server of the configured server list.
    * **round-robin**: Equally balance the requests for all configured servers.

<br>

Sponsored by <b><a href='http://vtlabs.org/'>VTLabs.org</a></b>, a <b><a href='http://www.voicetechnology.com.br/'>Voice Technology</a></b> division.