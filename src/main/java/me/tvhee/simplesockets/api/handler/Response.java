package me.tvhee.simplesockets.api.handler;

import me.tvhee.simplesockets.api.socket.Socket;

public interface Response
{
	void handle(Socket socket, String message);
}
