package me.tvhee.simplesockets.api.socket;

import java.io.Closeable;
import me.tvhee.simplesockets.api.connection.Connection;

public interface Socket
{
	String getName();

	void start();

	boolean isRunning();

	void sendMessage(String message);

	void sendMessage(String message, boolean duplicateCheck);

	void close();

	boolean isClosed();

	Connection getConnection();
}
