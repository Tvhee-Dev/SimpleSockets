package me.tvhee.simplesockets.connection;

import java.util.List;
import me.tvhee.simplesockets.handler.Handler;
import me.tvhee.simplesockets.socket.Socket;

public interface Connection
{
	void start();

	//Returns the socket with the specified name
	Socket getSocket(String name);

	//Returns the list of connected sockets. If this connection instance is a ClientConnection it is always safe
	//to call .get(0). The size will be always 1
	List<Socket> getSockets();

	void addHandler(Handler handler);

	void removeHandler(Handler handler);

	boolean isOpen();

	void close();
}
