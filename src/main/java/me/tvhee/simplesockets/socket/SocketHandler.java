package me.tvhee.simplesockets.socket;

public interface SocketHandler
{
	//Strings will behave as normal strings
	//Sent files will exist of <filename>/<fileContent> (content split by /n)
	default void handle(Socket socket, String message) {}

	default void connectionEstablished(Socket socket) {}

	default void connectionTerminated(Socket socket, SocketTerminateReason reason) {}
}
