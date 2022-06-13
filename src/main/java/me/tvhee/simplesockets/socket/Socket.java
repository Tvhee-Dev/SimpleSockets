package me.tvhee.simplesockets.socket;

import java.net.InetSocketAddress;
import me.tvhee.simplesockets.connection.Connection;

public interface Socket
{
	String getName();

	void setName(String name);

	//The address of the machine the socket is connected to
	InetSocketAddress getRemoteAddress();

	//THe address of THIS machine
	InetSocketAddress getLocalAddress();

	void start();

	boolean isRunning();

	void sendMessage(String message);

	void sendMessage(String message, boolean duplicateCheck);

	void close();

	boolean isClosed();

	Connection getConnection();
}
