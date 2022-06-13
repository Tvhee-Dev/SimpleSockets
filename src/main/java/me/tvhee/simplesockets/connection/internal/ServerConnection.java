package me.tvhee.simplesockets.connection.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.socket.Socket;
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
					Socket socket = new SocketConnection(serverSocket.accept(), this);
					socket.start();
					sockets.add(socket);
					handlers.forEach(handler -> handler.connectionEstablished(socket));
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
		handlers.forEach(handler -> handler.connectionTerminated(socket));
	}

	@Override
	public Socket getSocket(String name)
	{
		for(Socket socket : sockets)
		{
			if(socket.getName().equals(name))
				return socket;
		}

		return null;
	}

	@Override
	public List<Socket> getSockets()
	{
		return new ArrayList<>(sockets);
	}
}
