package me.tvhee.simplesockets.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import me.tvhee.simplesockets.connection.ServerConnection;
import me.tvhee.simplesockets.socket.Socket;

public final class SocketAcceptThread extends Thread
{
	private final ServerConnection serverConnection;
	private final ServerSocket serverSocket;
	private final List<Socket> sockets;

	public SocketAcceptThread(ServerConnection serverConnection, ServerSocket serverSocket, List<Socket> sockets)
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
			{
				Socket socket = new Socket(serverSocket.accept(), serverConnection);
				socket.start();
				sockets.add(socket);
			}
		}
		catch(IOException e)
		{
			if(serverConnection.isOpen())
				e.printStackTrace();
		}
	}
}
