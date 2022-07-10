package me.tvhee.simplesockets.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import me.tvhee.simplesockets.connection.SocketConnection;
import me.tvhee.simplesockets.connection.internal.ClientConnection;
import me.tvhee.simplesockets.connection.internal.AbstractConnection;
import me.tvhee.simplesockets.connection.internal.ServerConnection;
import me.tvhee.simplesockets.handler.SocketTermination;

public final class SocketImplementation implements Socket
{
	private final java.net.Socket socket;
	private final TimerTask authenticationTask;
	private final AbstractConnection connection;
	private String name;
	private boolean running;
	private Timer timer;
	private boolean authenticated;
	private BufferedReader socketInput;
	private PrintWriter socketOutput;
	private String lastMessage;

	public SocketImplementation(java.net.Socket socket, SocketConnection socketConnection)
	{
		this.socket = socket;
		this.connection = (AbstractConnection) socketConnection;
		this.name = socket.getInetAddress().toString();
		this.authenticationTask = new TimerTask()
		{
			@Override
			public void run()
			{
				if(!authenticated && !isClosed())
					close();
			}
		};
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		if(connection.getSocket(name) != null)
			throw new IllegalArgumentException("Socket name " + name + " is already in use!");

		this.name = name;
	}

	@Override
	public InetSocketAddress getRemoteAddress()
	{
		return (InetSocketAddress) socket.getRemoteSocketAddress();
	}

	@Override
	public InetSocketAddress getLocalAddress()
	{
		return (InetSocketAddress) socket.getLocalSocketAddress();
	}

	@Override
	public void start()
	{
		try
		{
			if(running)
				throw new IllegalArgumentException("Socket is already running on " + socket.getInetAddress().toString() + "!");

			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			running = true;

			new Thread(() ->
			{
				timer = new Timer();
				timer.schedule(authenticationTask, 5000);

				while(running)
				{
					try
					{
						String message;
						while((message = socketInput.readLine()) != null && running)
						{
							if(!authenticated)
							{
								if(message.startsWith("Secret ") && connection instanceof ServerConnection)
								{
									String inputSecret = message.split("Secret ", 2)[1];
									String serverSecret = connection.getSecretKey();

									if(serverSecret != null && !serverSecret.equals(inputSecret))
										continue;

									sendMessage("SecretAccepted");
									authenticated = true;
									connection.handleAuthenticated(this);
								}
								else if(message.equals("SecretAccepted") && connection instanceof ClientConnection)
								{
									authenticated = true;
									connection.handleAuthenticated(this);
								}

								continue;
							}

							if(message.equals("close"))
							{
								close(connection instanceof ServerConnection ? SocketTermination.TERMINATED_BY_CLIENT : SocketTermination.TERMINATED_BY_SERVER);
								break;
							}

							connection.notifyHandlers(this, message);
						}
					}
					catch(Exception e)
					{
						if(e instanceof SocketException && e.getMessage().equals("Connection reset"))
						{
							close(connection instanceof ServerConnection ? SocketTermination.TERMINATED_BY_CLIENT : SocketTermination.TERMINATED_BY_SERVER);
						}
						else if(running)
						{
							close(SocketTermination.ERROR);
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void sendMessage(String message)
	{
		sendMessage(message, true);
	}

	@Override
	public void sendMessage(String message, boolean duplicateCheck)
	{
		if(isClosed())
			throw new IllegalArgumentException("Socket is closed!");

		if(message.equals(lastMessage) && duplicateCheck)
			return;

		try
		{
			if(message.equals("close"))
				throw new IllegalArgumentException("If you'd like to close the connection, call close()!");

			socketOutput.println(message);
			lastMessage = message;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void close()
	{
		close(connection instanceof ServerConnection ? SocketTermination.TERMINATED_BY_SERVER : SocketTermination.TERMINATED_BY_CLIENT);
	}

	@Override
	public void close(SocketTermination reason)
	{
		try
		{
			if(isClosed())
				return;

			running = false;
			lastMessage = null;
			authenticated = false;

			if(socketOutput != null)
			{
				socketOutput.println("close");
				socketOutput.close();
				socketOutput = null;
			}

			if(!socket.isClosed())
				socket.close();

			if(socketInput != null)
			{
				socketInput.close();
				socketInput = null;
			}

			if(timer != null)
			{
				timer.cancel();
				timer = null;
			}

			connection.handleClose(this, reason);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isClosed()
	{
		return socket.isClosed() || !running;
	}

	@Override
	public SocketConnection getConnection()
	{
		return connection;
	}

	@Override
	public String toString()
	{
		return socket.toString();
	}
}
