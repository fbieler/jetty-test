package de.scoopsoftware;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

	static final Logger log = LoggerFactory.getLogger(TestServer.class);

	TestHandler handler;
	Server server;

	TestServer() {
		handler = new TestHandler();
		server = new Server(8080);
		server.setHandler(handler);
	}

	public static void main(String[] args) throws Exception {
		var server = new TestServer();
		server.server.start();
		server.server.join();
	}

	static class TestHandler extends Handler.Abstract {

		boolean readEntireRequest;

		void setReadEntireRequest(boolean readEntireRequest) {
			this.readEntireRequest = readEntireRequest;
		}

		@Override
		public boolean handle(Request request, Response response, Callback callback) throws InterruptedException {

			response.setStatus(204);
			if (readEntireRequest) {
				readEntireRequest(request);
			} else {
				readFirstChunk(request);
			}
			response.write(true, ByteBuffer.wrap(new byte[]{}), callback);
			return true;
		}

		void readEntireRequest(Request request) throws InterruptedException {
			while (true) {
				var c = request.read();
				if (c == null) {
					Thread.sleep(10);
					continue;
				}
				log.info("post {}", StandardCharsets.UTF_8.decode(c.getByteBuffer()));
				if (c.isLast()) {
					break;
				}
			}
		}

		void readFirstChunk(Request request) throws InterruptedException {
			while (true) {
				var c = request.read();
				if (c == null) {
					Thread.sleep(10);
					continue;
				}
				log.info("post {}", StandardCharsets.UTF_8.decode(c.getByteBuffer()));
				break;
			}
		}
	}
}
