Test to demonstrate that jetty can fail if a handler doesn't read the entirety of a chunked request

`TestServer` starts an embedded jetty server and listens for post requests.
By default, it only reads the first chunk of each incoming request before responding with 204.
Note that jersey and spring-mvc may behave similarly: For e.g. JSON payloads, a response is sent as soon as the request payload was fully parsed. This means that for HTTP/1.1 chunked transfer-encoding a response may be sent before the zero-length terminating chunk was received.

`TestClient` sends three post requests to the server.
By default, the terminating chunk of the second request is delayed by 100 milliseconds.

This setup causes jetty to hang after the second request, i.e. the second requests is responded to, but the third request is not processed.

For convenience a junit-test (`JettyTest`) is included to start both server and client.