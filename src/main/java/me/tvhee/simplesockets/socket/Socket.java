package me.tvhee.simplesockets.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import me.tvhee.simplesockets.connection.ServerConnection;
import me.tvhee.simplesockets.connection.SocketConnection;
import me.tvhee.simplesockets.thread.SocketInputThread;

public final class Socket
{
	private final SocketConnection connection;
	private String name;
	private java.net.Socket socket;
	private SocketInputThread socketInputThread;
	private boolean running;
	private BufferedReader socketInput;
	private PrintWriter socketOutput;
	private String lastMessage;

	public Socket(java.net.Socket socket, SocketConnection socketConnection)
	{
		this.socket = socket;
		this.connection = socketConnection;
		this.name = socket.getInetAddress().toString();
	}

	//Please do not call this method
	public void reconnect(java.net.Socket socket)
	{
		if(running)
			throw new IllegalArgumentException("Socket was never disconnected!");

		this.socket = socket;
		this.name = socket.getInetAddress().toString();
		this.start();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if(connection.getSocket(name) != null)
			throw new IllegalArgumentException("Socket name " + name + " is already in use!");

		this.name = name;
	}

	public InetSocketAddress getRemoteAddress()
	{
		return (InetSocketAddress) socket.getRemoteSocketAddress();
	}

	public InetSocketAddress getLocalAddress()
	{
		return (InetSocketAddress) socket.getLocalSocketAddress();
	}

	public void start()
	{
		try
		{
			if(running)
				throw new IllegalArgumentException("Socket is already running on " + socket.getInetAddress().toString() + "!");

			socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketOutput = new PrintWriter(socket.getOutputStream(), true);
			running = true;

			(socketInputThread = new SocketInputThread(this, socketInput)).start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isRunning()
	{
		return running;
	}

	public void sendFile(File file)
	{
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String nextLine;
			while((nextLine = reader.readLine()) != null)
				stringBuilder.append(nextLine).append("%n");

			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		sendMessage(file.getName() + "/" + stringBuilder);
	}

	public void sendMessage(String message)
	{
		sendMessage(message, true);
	}

	public void sendMessage(String message, boolean duplicateCheck)
	{
		if(isClosed())
			throw new IllegalArgumentException("Socket is closed!");

		if(message.equals(lastMessage) && duplicateCheck)
			return;

		try
		{
			if(message.equals("Close"))
				throw new IllegalArgumentException("If you'd like to close the connection, call close()!");

			socketOutput.println(message);
			lastMessage = message;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		close(connection instanceof ServerConnection ? SocketTerminateReason.TERMINATED_BY_SERVER : SocketTerminateReason.TERMINATED_BY_CLIENT);
	}

	public void close(SocketTerminateReason reason)
	{
		try
		{
			if(isClosed())
				return;

			running = false;
			lastMessage = null;

			if(socketInputThread != null)
			{
				socketInputThread.cancel();
				socketInputThread = null;
			}

			if(socketOutput != null)
			{
				socketOutput.println("Close");
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

			connection.handleClose(this, reason);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isClosed()
	{
		return socket.isClosed() || !running;
	}

	public String toString()
	{
		return socket.toString();
	}

	public SocketConnection getConnection()
	{
		return connection;
	}
}
