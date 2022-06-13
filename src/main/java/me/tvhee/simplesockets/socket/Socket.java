package me.tvhee.simplesockets.socket;

import java.net.SocketAddress;
import me.tvhee.simplesockets.connection.Connection;

public interface Socket
{
	String getName();

	void setName(String name);

	SocketAddress getAddress();

	void start();

	boolean isRunning();

	void sendMessage(String message);

	void sendMessage(String message, boolean duplicateCheck);

	void close();

	boolean isClosed();

	Connection getConnection();
}
