package me.tvhee.simplesockets.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.api.socket.Socket;
import me.tvhee.simplesockets.socket.SocketConnection;

public final class ServerConnection extends ConnectionAbstract
{
	private final List<Socket> sockets = new ArrayList<>();
	private final ServerSocket serverSocket;

	public ServerConnection(int serverPort) throws Exception
	{
		serverSocket = new ServerSocket(serverPort);
	}

	public void start()
	{
		running = true;

		new Thread(() ->
		{
			while(running)
			{
				try
				{
					Socket client = new SocketConnection(serverSocket.accept(), this);
					client.start();
					sockets.add(client);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void close()
	{
		super.close();

		for(Socket socket : new ArrayList<>(sockets))
		{
			if(!socket.isClosed())
				socket.close();
		}

		sockets.clear();
		running = false;
	}

	public void unregister(Socket socket)
	{
		if(!socket.isClosed() && socket.isRunning())
			throw new IllegalArgumentException("Socket is not closed!");

		sockets.remove(socket);
	}

	@Override
	public Socket getSocket()
	{
		throw new IllegalArgumentException("Please use getSockets() if using ServerConnection!");
	}

	@Override
	public List<Socket> getSockets()
	{
		return new ArrayList<>(sockets);
	}
}
