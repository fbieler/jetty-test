package de.scoopsoftware;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {

	static void readAndPrintResponse(Socket socket) throws IOException, InterruptedException {
		// this assumes that the response is sent in one chunk
		waitForResponse:
		{
			for (int i = 0; i < 500; ++i) {
				if (socket.getInputStream().available() > 0) {
					break waitForResponse;
				}
				Thread.sleep(10);
			}
			throw new RuntimeException("no response");
		}
		var response = new String(socket.getInputStream().readNBytes(socket.getInputStream().available()));
		System.out.println(response);
		if (!response.startsWith("HTTP/1.1 204") || !response.endsWith("\r\n\r\n")) {
			throw new RuntimeException("bad response: " + response);
		}
	}

	static void sendPostInTwoFlushes(PrintWriter writer) throws InterruptedException {
		System.out.println(">>> sending post");
		writer.print("""
				POST / HTTP/1.1\r
				Host: localhost\r
				Transfer-Encoding: chunked\r
				\r
				7\r
				payload\r
				""");
		writer.flush();

		Thread.sleep(100);

		writer.print("""
				0\r
				\r
				""");
		writer.flush();
	}

	static void sendPostInOneFlush(PrintWriter writer) {
		System.out.println(">>> sending post");
		writer.print("""
				POST / HTTP/1.1\r
				Host: localhost\r
				Transfer-Encoding: chunked\r
				\r
				7\r
				payload\r
				0\r
				\r
				""");
		writer.flush();
	}

	static void sendRequests(boolean sendPostInOneFlush) throws IOException, InterruptedException {

		try (var socket = new Socket("localhost", 8080)) {
			var writer = new PrintWriter(socket.getOutputStream());

			sendPostInOneFlush(writer);
			readAndPrintResponse(socket);

			if (sendPostInOneFlush) {
				sendPostInOneFlush(writer);
			} else {
				sendPostInTwoFlushes(writer);
			}
			readAndPrintResponse(socket);

			sendPostInOneFlush(writer);
			readAndPrintResponse(socket);
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		sendRequests(false);
	}
}

