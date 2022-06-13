package me.tvhee.simplesockets.socket;

import java.net.SocketAddress;
import me.tvhee.simplesockets.connection.Connection;

public interface Socket
{
	String getName();

	void setName(String name);

	//The address of the machine the socket is connected to
	SocketAddress getRemoteAddress();

	//THe address of THIS machine
	SocketAddress getLocalAddress();

	void start();

	boolean isRunning();

	void sendMessage(String message);

	void sendMessage(String message, boolean duplicateCheck);

	void close();

	boolean isClosed();

	Connection getConnection();
}
