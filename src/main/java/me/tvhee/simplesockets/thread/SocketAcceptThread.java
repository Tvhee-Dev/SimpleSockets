package me.tvhee.simplesockets.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;
import me.tvhee.simplesockets.connection.ServerConnection;
import me.tvhee.simplesockets.socket.Socket;

public final class SocketAcceptThread extends Thread
{
	private final ServerConnection serverConnection;
	private final ServerSocket serverSocket;
	private final Set<Socket> sockets;

	public SocketAcceptThread(ServerConnection serverConnection, ServerSocket serverSocket, Set<Socket> sockets)
	{
		this.serverConnection = serverConnection;
		this.serverSocket = serverSocket;
		this.sockets = sockets;
	}

	@Override
	public void run()
	{
		try
		{
			while(serverConnection.isOpen())
				new Socket(serverSocket.accept(), serverConnection).start();
		}
		catch(IOException e)
		{
			if(serverConnection.isOpen())
				e.printStackTrace();
		}
	}
}
