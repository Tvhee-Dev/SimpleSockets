package me.tvhee.simplesockets.socket;

import me.tvhee.simplesockets.connection.Connection;

public interface Socket
{
	String getName();

	void setName(String name);

	void start();

	boolean isRunning();

	void sendMessage(String message);

	void sendMessage(String message, boolean duplicateCheck);

	void close();

	boolean isClosed();

	Connection getConnection();
}
