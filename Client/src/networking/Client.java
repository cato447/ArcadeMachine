package networking;

import logging.ClientLogger;
import logging.LogType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private Socket serverSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private ClientLogger clientLogger;
    private static String name;
    private String serverName;
    private boolean success;
    private boolean authorizedToMove;

    public Client(String ip, int port, String name) {
        try {
            serverSocket = new Socket(ip, port);
            serverName = serverSocket.getRemoteSocketAddress().toString();
            out = new DataOutputStream(serverSocket.getOutputStream());
            in = new DataInputStream(serverSocket.getInputStream());
            clientLogger = new ClientLogger();
            success = true;
            authorizedToMove = false;
            this.name = name;
            clientLogger.printLog(String.format("Client with the name %s successfully initialized", name), success, LogType.Log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handshake() {
        try {
            out.writeInt(165313125);
            out.flush();
            success = in.readBoolean();
            if (success) {
                out.writeUTF(name);
                out.flush();
                clientLogger.printLog("You successfully connected to me", serverName, success, LogType.Log);
            } else {
                clientLogger.printLog("Connection failed try again", success, LogType.Log);
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void sendToServer(String message) {
        try {
            out.writeUTF(message);
            out.flush();
            boolean success = in.readBoolean();
            clientLogger.printLog(String.format("Sent the message: %s", message), serverName, success, LogType.Output);
            if(in.available() > 0){
                throw new Exception("More than just a boolean sent");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            this.exitProcess();
            this.printInputStream();
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void printInputStream(){
        try {
            byte[] inputBytes = in.readAllBytes();
            for (byte b: inputBytes) {
                if (b == 1 || b == 0){
                    System.out.println(b == 1);
                }
                System.out.print(Character.toString(b));
            }
            System.out.println(Arrays.toString(inputBytes));
            System.out.print("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMessage(){
        try {
            return in.available() < 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getResponse() {
        try {
            String message = in.readUTF();
            clientLogger.printLog(String.format("Message recieved: %s", message), serverName, true, LogType.Input);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean getBooleanResponse(String message) {
        try {
            boolean state = in.readBoolean();
            clientLogger.printLog(String.format("%s: %b", message, state), serverName, true, LogType.Input);
            return state;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPlayerOne(){
        this.sendToServer("isClientOne");
        boolean isClientOne = this.getBooleanResponse("isClientOne");
        return isClientOne;
    }

    public String getGameState(){
        try {
            out.writeUTF("gameState");
            return this.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "---------";
    }

    public boolean getServerType(){
        this.sendToServer("serverType");
        boolean serverType = this.getBooleanResponse("isSingleServer");
        return serverType;
    }

    public boolean getGameEnded() {
        this.sendToServer("gameEnded");
        boolean gameEnded = false;
        try {
            gameEnded = in.readBoolean();
            clientLogger.printLog(String.format("Game ended: %b", gameEnded), serverName, gameEnded, LogType.Input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameEnded;
    }

    public void exitProcess(){
        try {
            out.writeUTF("exit");
            out.flush();
            success = in.readBoolean();
            clientLogger.printLog("Closing connection to server", serverName, success, LogType.Log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetBoard(){
        try {
            out.writeUTF("reset");
            out.flush();
            success = in.readBoolean();
            clientLogger.printLog("Resetting board", serverName, success, LogType.Log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAuthorizedToMove(){
        return authorizedToMove;
    }

    public void setAuthorizedToMove(boolean isAuthorizedToMove){
        authorizedToMove = isAuthorizedToMove;
    }

    public void printLog(String message, boolean success, LogType logType){
        clientLogger.printLog(message, success, logType);
    }

    public boolean isConnected(){
        return serverSocket.isConnected();
    }


}