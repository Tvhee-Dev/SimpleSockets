package me.tvhee.simplesockets.socket;

import java.io.BufferedReader;
import java.io.PrintWriter;
import me.tvhee.simplesockets.connection.internal.ClientConnection;
import me.tvhee.simplesockets.connection.internal.ServerConnection;

public final class SocketThread extends Thread
{
	private final BufferedReader in;
	private final PrintWriter out;
	private final Socket socket;
	private boolean running;

	SocketThread(BufferedReader in, PrintWriter out, Socket socket)
	{
		this.in = in;
		this.out = out;
		this.socket = socket;
	}

	public void sendMessage(String message)
	{
		out.println(message);

		if(message.equals("close"))
			running = false;
	}

	@Override
	public void run()
	{
		try
		{
			running = true;

			String message;
			while((message = in.readLine()) != null && running)
			{
				if(socket.getConnection() instanceof ServerConnection)
					((ServerConnection) socket.getConnection()).notify(socket, message);
				else if(socket.getConnection() instanceof ClientConnection)
					((ClientConnection) socket.getConnection()).notify(socket, message);

				if(message.equals("close"))
					break;
			}

			if(!socket.isClosed())
				socket.close();

			in.close();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
