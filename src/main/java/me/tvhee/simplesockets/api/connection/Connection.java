package me.tvhee.simplesockets.api.connection;

import java.util.List;
import me.tvhee.simplesockets.api.handler.Response;
import me.tvhee.simplesockets.api.socket.Socket;

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
