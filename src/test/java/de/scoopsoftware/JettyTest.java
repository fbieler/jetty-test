package de.scoopsoftware;

import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class JettyTest {

	static TestServer server;

	@BeforeAll
	static void startServer() throws Exception {
		CompletableFuture<Void> serverStarted = new CompletableFuture<>();
		server = new TestServer();
		server.server.addEventListener(new Listener() {
			@Override
			public void lifeCycleStarted(LifeCycle event) {
				serverStarted.complete(null);
				server.server.removeEventListener(this);
			}
		});
		server.server.start();
		serverStarted.get();
	}

	@Test
	@Timeout(10)
	void test() throws Exception {
		server.handler.setReadEntireRequest(false);
		TestClient.sendRequests(false);
	}

	@Test
	@Timeout(10)
	void sendRequestInOnFlush() throws Exception {
		server.handler.setReadEntireRequest(false);
		TestClient.sendRequests(true);
	}

	@Test
	@Timeout(10)
	void readEntireRequest() throws Exception {
		server.handler.setReadEntireRequest(true);
		TestClient.sendRequests(false);
	}
}