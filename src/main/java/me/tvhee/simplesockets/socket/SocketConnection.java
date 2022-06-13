package me.tvhee.simplesockets.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketAddress;
import me.tvhee.simplesockets.connection.internal.ClientConnection;
import me.tvhee.simplesockets.connection.Connection;
import me.tvhee.simplesockets.connection.internal.ServerConnection;

public final class SocketConnection implements Socket
{
	private final java.net.Socket socket;
	private final Connection connection;
	private String name;
	private boolean running;
	private SocketThread socketThread;
	private String lastMessage;

	public SocketConnection(java.net.Socket socket, Connection connection)
	{
		this.socket = socket;
		this.connection = connection;
		this.name = socket.getInetAddress().toString();
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		if(connection.getSocket(name) != null)
			throw new IllegalArgumentException("Socket name " + name + " is already in use!");

		this.name = name;

		if(this.socketThread != null)
			this.socketThread.setName(name);
	}

	@Override
	public SocketAddress getAddress()
	{
		return socket.getRemoteSocketAddress();
	}

	@Override
	public void start()
	{
		try
		{
			if(running)
				throw new IllegalArgumentException("Socket is already running on " + socket.getInetAddress().toString() + "!");

			socketThread = new SocketThread(new BufferedReader(new InputStreamReader(socket.getInputStream())),
					new PrintWriter(socket.getOutputStream(), true), this);
			socketThread.setName(name);
			socketThread.start();

			running = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void sendMessage(String message)
	{
		sendMessage(message, true);
	}

	@Override
	public void sendMessage(String message, boolean duplicateCheck)
	{
		if(!running)
			throw new IllegalArgumentException("Socket is not running! Please call start() first");

		if(message.equals(lastMessage) && duplicateCheck)
			return;

		try
		{
			if(message.equals("close"))
				throw new IllegalArgumentException("If you'd like to close the connection, call close()!");

			socketThread.sendMessage(message);
			lastMessage = message;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		try
		{
			socketThread.sendMessage("close");
			running = false;
			socketThread = null;
			socket.close();

			if(connection instanceof ServerConnection)
				((ServerConnection) connection).unregister(this);
			else if(connection instanceof ClientConnection)
				connection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isClosed()
	{
		return socket.isClosed();
	}

	@Override
	public Connection getConnection()
	{
		return connection;
	}
}
