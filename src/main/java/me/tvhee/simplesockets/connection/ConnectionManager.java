package me.tvhee.simplesockets.connection;

import me.tvhee.simplesockets.connection.internal.ClientConnection;
import me.tvhee.simplesockets.connection.internal.ServerConnection;

public final class ConnectionManager
{
	private ConnectionManager() {}

	public static Connection serverConnection(int serverPort)
	{
		try
		{
			return new ServerConnection(serverPort);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Could not connect!", e);
		}
	}

	public static Connection clientConnection(String serverIP, int serverPort)
	{
		try
		{
			return new ClientConnection(serverIP, serverPort);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Could not connect!", e);
		}
	}
}
