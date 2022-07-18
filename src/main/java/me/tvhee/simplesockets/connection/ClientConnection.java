package me.tvhee.simplesockets.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;

public final class ClientConnection extends SocketConnection
{
	private final String serverIP;
	private final int serverPort;
	private Timer reconnectTask;
	private long reconnect;
	private String secretKey;
	private Socket socket;

	ClientConnection(String serverIP, int serverPort)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	@Override
	public void start()
	{
		try
		{
			Socket socket = new Socket(new java.net.Socket(serverIP, serverPort), this);
			socket.start();
			socket.sendMessage("Secret " + secretKey);

			running = true;
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Could not connect!", e);
		}
	}

	@Override
	public void close()
	{
		close(SocketTerminateReason.TERMINATED_BY_CLIENT);
	}

	private void close(SocketTerminateReason reason)
	{
		if(!running)
			return;

		running = false;

		if(reconnectTask != null)
		{
			reconnectTask.cancel();
			reconnectTask = null;
		}

		if(socket == null)
			return;

		if(!socket.isClosed())
			socket.close();

		socketHandlers.forEach(handler -> handler.connectionTerminated(socket, reason));
		socket = null;
	}

	@Override
	public void notifyHandlers(Socket socket, String message)
	{
		if(message.equals("Close"))
			reconnect = -1;

		super.notifyHandlers(socket, message);
	}

	@Override
	public void handleAuthenticated(Socket socket)
	{
		this.socket = socket;
		this.socketHandlers.forEach(handler -> handler.connectionEstablished(socket));
	}

	@Override
	public void handleClose(Socket socket, SocketTerminateReason reason)
	{
		if(!socket.isClosed() && socket.isRunning())
			throw new IllegalArgumentException("Socket is not closed!");

		if(this.socket == null)
			return;

		if(reason == SocketTerminateReason.TERMINATED_BY_SERVER || reason == SocketTerminateReason.NO_RESPONSE)
		{
			reconnectTask = new Timer();
			reconnectTask.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					if(reconnect > 0)
					{
						try
						{
							java.net.Socket connectedSocket = new java.net.Socket(serverIP, serverPort);
							socket.reconnect(connectedSocket);
							socket.sendMessage("Secret " + secretKey);
							return;
						}
						catch(Exception ignored)
						{
						}
					}

					close(reason);
				}
			}, reconnect);
		}
		else
		{
			close(reason);
		}
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
		if(socket == null)
			return new ArrayList<>();
		else
			return Collections.singletonList(socket);
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

	public void setReconnectTime(long reconnectTime)
	{
		if(reconnectTime < 0)
			reconnectTime = reconnectTime * -1;

		this.reconnect = reconnectTime;
	}

	public long getReconnectTime()
	{
		return reconnect;
	}
}
