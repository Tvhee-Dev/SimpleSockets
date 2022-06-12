package me.tvhee.simplesockets.connection.internal;

import java.util.List;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketConnection;

public final class ClientConnection extends ConnectionAbstract
{
	private final Socket socket;

	public ClientConnection(String serverIP, int serverPort) throws Exception
	{
		this.socket = new SocketConnection(new java.net.Socket(serverIP, serverPort), this);
	}

	@Override
	public void start()
	{
		this.running = true;
		this.socket.start();
	}

	@Override
	public void close()
	{
		super.close();

		if(!socket.isClosed())
			socket.close();

		this.running = false;
	}

	@Override
	public Socket getSocket()
	{
		return socket;
	}

	@Override
	public List<Socket> getSockets()
	{
		throw new IllegalArgumentException("Please use getSocket() if using ClientConnection!");
	}
}
