package me.tvhee.simplesockets.connection;

import java.util.List;
import me.tvhee.simplesockets.handler.Response;
import me.tvhee.simplesockets.socket.Socket;

public interface Connection
{
	void start();

	Socket getSocket();

	List<Socket> getSockets();

	void addHandler(Response handler);

	void removeHandler(Response handler);

	boolean isOpen();

	void close();
}
