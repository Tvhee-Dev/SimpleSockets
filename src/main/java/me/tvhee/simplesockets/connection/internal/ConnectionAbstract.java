package me.tvhee.simplesockets.connection.internal;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.handler.SocketHandler;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.connection.Connection;

public abstract class ConnectionAbstract implements Connection
{
	protected final List<SocketHandler> socketHandlers = new ArrayList<>();
	protected boolean running;

	ConnectionAbstract() {}

	@Override
	public void addHandler(SocketHandler socketHandler)
	{
		socketHandlers.add(socketHandler);
	}

	@Override
	public void removeHandler(SocketHandler socketHandler)
	{
		socketHandlers.remove(socketHandler);
	}

	public void notify(Socket socket, String message)
	{
		socketHandlers.forEach(socketHandler -> socketHandler.handle(socket, message));
	}

	@Override
	public boolean isOpen()
	{
		return running;
	}

	@Override
	public void close()
	{
		this.running = false;
	}
}
