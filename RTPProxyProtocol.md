The available RTPProxy server commands are listed in http://www.b2bua.org/wiki/RTPproxy/Protocol but command and response format details are missing. So this document describes what we figured out from source code analysis and tests.


# Command Format #

The general command format is:

```
COOKIE COMMAND ARGUMENTS
```

  * _COOKIE_ is an arbitrary string identifying the the command. The response for that command will have the same prefix.

  * _COMMAND_ and _ARGUMENTS_ are those described in the [RTPProxy Protocol documentation](http://www.b2bua.org/wiki/RTPproxy/Protocol).

<br>

<h1>Response Codes</h1>

<table><thead><th> <b>Code</b> </th><th> <b>Command</b> </th><th> <b>Description</b> </th><th> <b>Most common cause</b> </th></thead><tbody>
<tr><td> E0 </td><td> any </td><td> Syntax error </td><td>  </td></tr>
<tr><td> E3 </td><td> any </td><td> Unknown command </td><td>  </td></tr>
<tr><td> E8 </td><td> D <i>(delete)</i> </td><td> delete request failed: session <i>CALL_ID</i>, tags <i>FROMTAG</i>/<i>TOTAG</i> not found </td><td> Session timed out in the RTPProxy server. </td></tr>
<tr><td> E10 </td><td> U <i>(update)</i> </td><td> can't bind to the IPv4 port XXXXX: Cannot assign requested address </td><td> RTPProxy server was bound to an invalid address (-l parameter). The listen address is not verified in the server initialization. </td></tr>