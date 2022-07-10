package me.tvhee.simplesockets.connection.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.tvhee.simplesockets.handler.SocketTermination;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketImplementation;

public final class ClientConnection extends AbstractConnection
{
	private final String serverIP;
	private final int serverPort;
	private long reconnect;
	private String secretKey;
	private Socket socket;

	public ClientConnection(String serverIP, int serverPort)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}

	@Override
	public void start()
	{
		try
		{
			SocketImplementation socketImplementation = new SocketImplementation(new java.net.Socket(serverIP, serverPort), this);
			socketImplementation.start();
			socketImplementation.sendMessage("Secret " + secretKey);
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
		if(!running)
			return;

		running = false;

		if(socket == null)
			return;

		if(!socket.isClosed())
		{
			socket.close();
			socket = null;
		}
	}

	@Override
	public void notifyHandlers(Socket socket, String message)
	{
		if(message.equals("close"))
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
	public void handleClose(Socket socket, SocketTermination reason)
	{
		if(!socket.isClosed() && socket.isRunning())
			throw new IllegalArgumentException("Socket is not closed!");

		if(this.socket == null)
			return;

		Runnable finishRunnable = () ->
		{
			running = false;
			ClientConnection.this.socket = null;
			socketHandlers.forEach(handler -> handler.connectionTerminated(socket, reason));
		};

		if(reason == SocketTermination.TERMINATED_BY_SERVER)
		{
			new Thread(() ->
			{
				if(reconnect > 0)
				{
					System.out.println("[Warning] Lost connection with " + socket.getRemoteAddress() + "!");

					try
					{
						Thread.sleep(reconnect);
						ClientConnection.this.start();
						System.out.println("[Success] Connected successfully back with " + socket.getRemoteAddress());
						return;
					}
					catch(Exception e)
					{
						System.err.println("[Error] Could not reconnect to " + socket.getRemoteAddress() + "!");
					}
				}

				finishRunnable.run();
			}).start();
		}
		else
		{
			finishRunnable.run();
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

	@Override
	public void setReconnectTime(long reconnectTime)
	{
		if(reconnectTime < 0)
			reconnectTime = reconnectTime * -1;

		this.reconnect = reconnectTime;
	}

	@Override
	public long getReconnectTime()
	{
		return reconnect;
	}
}
