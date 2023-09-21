package me.tvhee.simplesockets.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;
import me.tvhee.simplesockets.thread.SocketAcceptThread;

public final class ServerConnection extends SocketConnection
{
	private final Set<Socket> sockets = new HashSet<>();
	private final int serverPort;
	private String secretKey;
	private ServerSocket serverSocket;

	ServerConnection(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public void start()
	{
		try
		{
			serverSocket = new ServerSocket(serverPort);
			running = true;

			new SocketAcceptThread(this, serverSocket, sockets).start();
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
			if(!running)
				return;

			running = false;

			for(Socket socket : new ArrayList<>(sockets))
			{
				if(socket != null && !socket.isClosed())
					socket.close(SocketTerminateReason.TERMINATED_BY_SERVER);
			}

			if(serverSocket != null)
			{
				serverSocket.close();
				serverSocket = null;
			}
		}
		catch(IOException ignored)
		{
		
		}
	}

	@Override
	public void handleAuthenticated(Socket socket)
	{
		sockets.add(socket);
		socketHandlers.forEach(handler -> handler.connectionEstablished(socket));
	}

	@Override
	public void handleClose(Socket socket, SocketTerminateReason reason)
	{
		if(!socket.isClosed() || socket.isRunning())
			throw new IllegalArgumentException("Socket is not closed!");

		sockets.remove(socket);
		socketHandlers.forEach(handler -> handler.connectionTerminated(socket, reason));
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
	public Socket getRandom()
	{
		List<Socket> list = new ArrayList<>(sockets);
		Collections.shuffle(list);

		if(list.isEmpty())
			return null;

		return list.get(0);
	}

	@Override
	public Set<Socket> getSockets()
	{
		return new HashSet<>(sockets);
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
		throw new UnsupportedOperationException();
	}

	@Override
	public long getReconnectTime()
	{
		throw new UnsupportedOperationException();
	}
}
