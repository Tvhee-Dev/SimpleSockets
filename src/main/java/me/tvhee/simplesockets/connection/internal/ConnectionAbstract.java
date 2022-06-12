package me.tvhee.simplesockets.connection.internal;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.handler.Handler;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.connection.Connection;

public abstract class ConnectionAbstract implements Connection
{
	protected final List<Handler> handlers = new ArrayList<>();
	protected boolean running;

	ConnectionAbstract() {}

	@Override
	public void addHandler(Handler handler)
	{
		handlers.add(handler);
	}

	@Override
	public void removeHandler(Handler handler)
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
