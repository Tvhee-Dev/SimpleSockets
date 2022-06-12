package me.tvhee.simplesockets.connection;

import java.util.List;
import me.tvhee.simplesockets.handler.Handler;
import me.tvhee.simplesockets.socket.Socket;

public interface Connection
{
	void start();

	Socket getSocket();

	List<Socket> getSockets();

	void addHandler(Handler handler);

	void removeHandler(Handler handler);

	boolean isOpen();

	void close();
}
