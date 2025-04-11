import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
  public ServerSocket serverSocket = null;
  public Queue<Socket> waitingClients = new LinkedList<>();

  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.serverSocket = new ServerSocket(5000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    while (true) {
      Socket clientSocket = null;
      try {
        clientSocket = server.serverSocket.accept();
        clientSocket.setKeepAlive(true);
        server.waitingClients.add(clientSocket);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("connection established :" + clientSocket);
      if (server.waitingClients.size() > 1) {
        System.out.println("paired clients");
        new GameServer(server.waitingClients.remove(), server.waitingClients.remove());
      }
    }
  }
}

class GameServer{
  Socket whiteClient = null;
  Socket blackClient = null;
  PrintWriter whiteWriter = null;
  PrintWriter blackWriter = null;

  public GameServer(Socket whiteClient, Socket blackClient) {
    this.whiteClient = whiteClient;
    this.blackClient = blackClient;

    try {
      whiteWriter = new PrintWriter(whiteClient.getOutputStream());
      blackWriter = new PrintWriter(blackClient.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
    }

    sendColorMessage(whiteWriter, "U white");
    sendColorMessage(blackWriter, "U black");
    Thread whiteListener = new Thread(new ClientHandler(whiteClient, true));
    Thread blackListener = new Thread(new ClientHandler(blackClient, false));
    whiteListener.start();
    blackListener.start();
  }

  private void sendColorMessage(PrintWriter writer, String color) {
    writer.println(color);
    writer.flush();
  }

  class ClientHandler implements Runnable {
    BufferedReader reader;
    Socket sock;
    boolean color; 

    public ClientHandler(Socket clientSocket, boolean color) {
      this.color = color ;
      try {
        sock = clientSocket;
        InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(isReader);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          System.out.println("read " + message);
          tellEveryone(message, this.color);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void tellEveryone(String message, boolean color){
    if(color){
        blackWriter.println(message);
        blackWriter.flush();
    }else{
        whiteWriter.println(message);
        whiteWriter.flush();
    }
  }
}
