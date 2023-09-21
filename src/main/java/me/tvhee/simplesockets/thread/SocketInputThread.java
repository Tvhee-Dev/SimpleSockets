package me.tvhee.simplesockets.thread;

import java.io.BufferedReader;
import java.net.SocketException;
import java.util.Timer;
import me.tvhee.simplesockets.connection.ServerConnection;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;
import me.tvhee.simplesockets.task.SocketAuthenticationTask;
import me.tvhee.simplesockets.task.SocketKeepAliveTask;

public final class SocketInputThread extends Thread
{
	private final Socket socket;
	private final BufferedReader inputStream;
	private final SocketAuthenticationTask authenticationTask;
	private final SocketKeepAliveTask socketKeepAliveTask;
	private Timer timer;

	public SocketInputThread(Socket socket, BufferedReader inputStream)
	{
		this.socket = socket;
		this.inputStream = inputStream;
		this.authenticationTask = new SocketAuthenticationTask(socket);
		this.socketKeepAliveTask = new SocketKeepAliveTask(socket);
	}

	@Override
	public void run()
	{
		if(timer != null)
			cancel();
		
		timer = new Timer();
		
		if(socket.getConnection().getSecretKey() != null)
			timer.schedule(authenticationTask, 5000);
		
		timer.schedule(socketKeepAliveTask, 5001, 5000);
		
		while(socket.isRunning())
		{
			try
			{
				String message;
				while((message = inputStream.readLine()) != null && socket.isRunning())
				{
					socketKeepAliveTask.keepAlive(message);
					
					if(message.equals("KeepAlive") || message.equals("KeepAliveResponse"))
						continue;
					
					if(message.startsWith("Secret ") || message.equals("SecretAccepted"))
					{
						authenticationTask.authenticate(message);
						continue;
					}
					
					if(message.equals("Close"))
					{
						socket.close(socket.getConnection() instanceof ServerConnection ? SocketTerminateReason.TERMINATED_BY_CLIENT : SocketTerminateReason.TERMINATED_BY_SERVER);
						break;
					}
					
					socket.getConnection().notifyHandlers(socket, message);
				}
			}
			catch(Exception e)
			{
				if(e instanceof SocketException && e.getMessage().equals("Connection reset"))
				{
					socket.close(socket.getConnection() instanceof ServerConnection ? SocketTerminateReason.TERMINATED_BY_CLIENT : SocketTerminateReason.TERMINATED_BY_SERVER);
				}
				else if(socket.isRunning())
				{
					socket.close(SocketTerminateReason.ERROR);
					e.printStackTrace();
				}
			}
		}
	}

	public void cancel()
	{
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}
}
