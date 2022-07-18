package me.tvhee.simplesockets.task;

import java.util.TimerTask;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;

public final class SocketKeepAliveTask extends TimerTask
{
	private final Socket socket;
	private long lastKeepAliveSent;
	private long lastKeepAliveResponse;

	public SocketKeepAliveTask(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		if(lastKeepAliveResponse - lastKeepAliveSent < -1000)
		{
			socket.close(SocketTerminateReason.NO_RESPONSE);
			return;
		}

		if(System.currentTimeMillis() - lastKeepAliveSent >= 5000)
		{
			socket.sendMessage("KeepAlive", false);
			lastKeepAliveSent = System.currentTimeMillis();
		}
	}

	public void keepAlive(String message)
	{
		if(message.equals("KeepAlive"))
			socket.sendMessage("KeepAliveResponse", false);
		else if(message.equals("KeepAliveResponse"))
			this.lastKeepAliveResponse = System.currentTimeMillis();
	}
}
