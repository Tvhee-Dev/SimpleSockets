package me.tvhee.simplesockets.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import me.tvhee.simplesockets.api.socket.Socket;
import me.tvhee.simplesockets.connection.ClientConnection;
import me.tvhee.simplesockets.api.connection.Connection;
import me.tvhee.simplesockets.connection.ServerConnection;

public final class SocketConnection implements Socket
{
	private final java.net.Socket socket;
	private final Connection connection;
	private boolean running;
	private SocketThread socketThread;
	private String lastMessage;

	public SocketConnection(java.net.Socket socket, Connection connection)
	{
		this.socket = socket;
		this.connection = connection;
	}

	@Override
	public String getName()
	{
		return socket.getInetAddress().toString();
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
