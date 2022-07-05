package me.tvhee.simplesockets.connection;

import java.util.List;
import me.tvhee.simplesockets.handler.SocketHandler;
import me.tvhee.simplesockets.socket.Socket;

public interface SocketConnection
{
	void start();

	//Returns the socket with the specified name
	Socket getSocket(String name);

	//Returns the list of connected sockets. If this connection instance is a ClientConnection it is always safe
	//to call .get(0). The size will be always 1
	List<Socket> getSockets();

	//This key should be the same at the client / server for security
	void setSecretKey(String key);

	String getSecretKey();

	void addHandler(SocketHandler socketHandler);

	void removeHandler(SocketHandler socketHandler);

	boolean isOpen();

	//Client only: Try to reconnect after ... milliseconds to the server
	void setReconnectTime(long reconnectTime);

	long getReconnectTime();

	void close();
}
