package me.tvhee.simplesockets.handler;

import me.tvhee.simplesockets.socket.Socket;

public interface Response
{
	void handle(Socket socket, String message);
}
