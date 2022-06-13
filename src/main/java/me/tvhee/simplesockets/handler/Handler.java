package me.tvhee.simplesockets.handler;

import me.tvhee.simplesockets.socket.Socket;

public interface Handler
{
	void handle(Socket socket, String message);

	default void connectionEstablished(Socket socket)
	{
	}

	default void connectionTerminated(Socket socket)
	{
	}
}
