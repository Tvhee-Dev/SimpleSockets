# SimpleSockets

## Usage
1. To start connecting with sockets, create your connection instance
```java
Connection connection = ConnectionManager.serverConnection(port); //Server side
Connection connection = ConnectionManager.clientConnection(IP, port); //Client side
```
2. Create a new class implementing `SocketHandler`
```java
public class MySocketHandler implements SocketHandler
{
        @Override
        public void handle(Socket socket, String message)
        {
                 //Your code here
        }
}
```
3. Register your handler in your connection instance
```java
connection.registerHandler(new MySocketHandler());
```
4. You are ready to go! Start the connection
```java
connection.start();
```

## Important
1. I have built-in a duplicate message detector. It is important to prevent duplicates to prevent endless loops for example
2. If you want to disable the duplication checker for ONE message use
```java
socket.sendMessage(message, false);
```
3. Please make sure to **close** the connection if the plugin gets disabled. This is important to prevent errors in the socket pipeline
```java
connection.close();
```
