package me.tvhee.simplesockets.api.connection;

import me.tvhee.simplesockets.connection.ClientConnection;
import me.tvhee.simplesockets.connection.ServerConnection;

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
