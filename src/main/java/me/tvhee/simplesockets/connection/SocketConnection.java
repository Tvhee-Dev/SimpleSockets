package me.tvhee.simplesockets.connection;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;
import me.tvhee.simplesockets.socket.SocketHandler;

public abstract class SocketConnection
{
	protected final List<SocketHandler> socketHandlers = new ArrayList<>();
	protected boolean running;

	SocketConnection() {}

	public static SocketConnection clientConnection(String host, int port)
	{
		return new ClientConnection(host, port);
	}

	public static SocketConnection serverConnection(int port)
	{
		return new ServerConnection(port);
	}

	public abstract void handleAuthenticated(Socket socket);

	public abstract void handleClose(Socket socket, SocketTerminateReason reason);

	public abstract void start();

	//Returns the socket with the specified name
	public abstract Socket getSocket(String name);

	//Returns the list of connected sockets. If this connection instance is a ClientConnection it is safe
	//to call .get(0) if .isOpen() returns true. The size will be always 1
	public abstract List<Socket> getSockets();

	//This key should be the same at the client / server for security
	public abstract void setSecretKey(String key);

	public abstract String getSecretKey();

	public abstract void setReconnectTime(long reconnectTime);

	public abstract long getReconnectTime();

	public abstract void close();

	public void addHandler(SocketHandler socketHandler)
	{
		socketHandlers.add(socketHandler);
	}

	public void removeHandler(SocketHandler socketHandler)
	{
		socketHandlers.remove(socketHandler);
	}

	public void notifyHandlers(Socket socket, String message)
	{
		socketHandlers.forEach(socketHandler -> socketHandler.handle(socket, message));
	}

	public boolean isOpen()
	{
		return running;
	}
}
