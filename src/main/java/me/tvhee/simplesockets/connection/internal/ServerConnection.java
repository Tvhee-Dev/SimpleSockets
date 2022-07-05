package me.tvhee.simplesockets.connection.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.handler.SocketTermination;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketImplementation;

public final class ServerConnection extends AbstractConnection
{
	private final List<Socket> sockets = new ArrayList<>();
	private final int serverPort;
	private String secretKey;
	private ServerSocket serverSocket;

	public ServerConnection(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public void start()
	{
		try
		{
			this.serverSocket = new ServerSocket(serverPort, 1);

			new Thread(() ->
			{
				try
				{
					running = true;

					while(running)
						new SocketImplementation(serverSocket.accept(), ServerConnection.this).start();

					serverSocket = null;
				}
				catch(IOException e)
				{
					if(running)
						e.printStackTrace();
				}
			}).start();
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
			running = false;

			for(Socket socket : new ArrayList<>(sockets))
			{
				if(socket != null && !socket.isClosed())
					socket.close();
			}

			serverSocket.close();
			serverSocket = null;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void handleAuthenticated(Socket socket)
	{
		sockets.add(socket);
		socketHandlers.forEach(handler -> handler.connectionEstablished(socket));
	}

	@Override
	public void handleClose(Socket socket, SocketTermination reason)
	{
		if(!socket.isClosed() && socket.isRunning())
			throw new IllegalArgumentException("Socket is not closed!");

		if(sockets.contains(socket))
		{
			sockets.remove(socket);
			socketHandlers.forEach(handler -> handler.connectionTerminated(socket, reason));
		}
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

	@Override
	public void setSecretKey(String key)
	{
		if(key != null && key.isEmpty())
			key = null;

		this.secretKey = key;
	}

	@Override
	public String getSecretKey()
	{
		return secretKey;
	}

	@Override
	public void setReconnectTime(long reconnectTime)
	{
		throw new IllegalArgumentException("ReconnectTime is only supported by client connections!");
	}

	@Override
	public long getReconnectTime()
	{
		throw new IllegalArgumentException("ReconnectTime is only supported by client connections!");
	}
}
