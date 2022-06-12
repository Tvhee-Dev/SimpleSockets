package me.tvhee.simplesockets.connection;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.api.connection.Connection;
import me.tvhee.simplesockets.api.handler.Response;
import me.tvhee.simplesockets.api.socket.Socket;

public abstract class ConnectionAbstract implements Connection
{
	protected final List<Response> handlers = new ArrayList<>();
	protected boolean running;

	ConnectionAbstract() {}

	@Override
	public void addHandler(Response handler)
	{
		handlers.add(handler);
	}

	@Override
	public void removeHandler(Response handler)
	{
		handlers.remove(handler);
	}

	public void notify(Socket socket, String message)
	{
		handlers.forEach(handler -> handler.handle(socket, message));
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
