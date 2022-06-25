package me.tvhee.simplesockets.connection.internal;

import java.util.Collections;
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
		this.socketHandlers.forEach(handler -> handler.connectionEstablished(this.socket));
	}

	@Override
	public void close()
	{
		super.close();

		if(!socket.isClosed())
			socket.close();

		this.socketHandlers.forEach(handler -> handler.connectionTerminated(this.socket));
		this.running = false;
	}

	@Override
	public Socket getSocket(String name)
	{
		if(socket.getName().equals(name))
			return socket;

		return null;
	}

	@Override
	public List<Socket> getSockets()
	{
		return Collections.singletonList(socket);
	}
}
