package me.tvhee.simplesockets.task;

import java.util.TimerTask;
import me.tvhee.simplesockets.connection.ClientConnection;
import me.tvhee.simplesockets.connection.ServerConnection;
import me.tvhee.simplesockets.socket.Socket;
import me.tvhee.simplesockets.socket.SocketTerminateReason;

public final class SocketAuthenticationTask extends TimerTask
{
	private final Socket socket;
	private boolean authenticated;

	public SocketAuthenticationTask(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	public void run()
	{
		socket.close(SocketTerminateReason.NOT_AUTHENTICATED);
	}

	public void authenticate(String message)
	{
		if(authenticated)
			return;

		if(message.startsWith("Secret ") && socket.getConnection() instanceof ServerConnection)
		{
			String inputSecret = message.split("Secret ", 2)[1];
			String serverSecret = socket.getConnection().getSecretKey();

			if(serverSecret != null && !serverSecret.equals(inputSecret))
				return;

			socket.sendMessage("SecretAccepted");
		}

		if(!message.equals("SecretAccepted") && socket.getConnection() instanceof ClientConnection)
			return;

		socket.getConnection().handleAuthenticated(socket);
		authenticated = true;
		cancel();
	}
}
