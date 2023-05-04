package com.learning.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server extends JFrame {
    private static final int PORT = 8888;
    private JTextArea serverTextArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(serverTextArea);
    private JPanel buttonTool = new JPanel();
    private JButton startButton = new JButton("Launch");
    private JButton stopButton = new JButton("Stop");

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ArrayList<ClientConnection> connectionsList = new ArrayList<>();

    private DataInputStream dis = null;
    private boolean isLaunched = false;

    class ClientConnection implements Runnable{
        Socket socket = null;

        public ClientConnection(Socket socket) {
            this.socket = socket;
            (new Thread(this)).start();
        }

        // Simultaneously receive messages from all clients
        @Override
        public void run() {
            try {
                DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                // The server can receive all messages sent by each client from each connection
                while(isLaunched) {
                    String messageReceived = dataIn.readUTF();
                    System.out.println("Server received the message: " + messageReceived);
                    String clientIPAndPort = socket.getInetAddress() + "|" + socket.getPort();
                    String messageToSend = clientIPAndPort + ": " + messageReceived + "\n";
                    serverTextArea.append(messageToSend); // show the message on Server side

                    Iterator<ClientConnection> iterator = connectionsList.iterator();
                    while(iterator.hasNext()) {
                        ClientConnection connection = iterator.next();
                        connection.send(messageToSend);
                    }
                }
            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("The client has disconneted.");
                serverTextArea.append(socket.getInetAddress() + "|" + socket.getPort() + " is disconnected");
            }
        }

        public void send(String message) {
            try {
                DataOutputStream dataOut = new DataOutputStream(this.socket.getOutputStream());
                dataOut.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
    }

    public Server() throws HeadlessException {
        this.setTitle("Server log");
        this.add(scrollPane, BorderLayout.CENTER);
        buttonTool.add(startButton);
        buttonTool.add(stopButton);
        this.add(buttonTool, BorderLayout.SOUTH);

        this.setBounds(0, 0, 500, 500);

        if(isLaunched) {
            serverTextArea.append("Server is running.\n");
        } else {
            serverTextArea.append("Server hasn't launched yet. Please launch the server.\n");
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isLaunched = false;
                try {
                    if(serverSocket != null) serverSocket.close();
                    serverTextArea.append("The server is disconnected.");
                    System.exit(0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(serverSocket != null) {
                        serverSocket.close();
                        isLaunched = false;
                    }
                    serverTextArea.append("The server is disconnected.");
                    System.exit(0);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if(serverSocket == null) {
                        serverSocket = new ServerSocket(PORT);
                    }
                    isLaunched = true;
                    System.out.println("The server has launched");
                    serverTextArea.append("The server has launched \n");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        serverTextArea.setEditable(false);
        this.setVisible(true);
        startServer();
    }

    public void startServer() {
        try {
            try {
                serverSocket = new ServerSocket(PORT);
                isLaunched = true;
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            // connecting to multiple clients
            while (isLaunched) {
                System.out.println("always true");
                Socket newSocket = serverSocket.accept();
                connectionsList.add(new ClientConnection(newSocket));
                System.out.println("A client has connected to the server: " + newSocket.getInetAddress() + "/" + newSocket.getPort());
                serverTextArea.append("A client has connected to the server: " + newSocket.getInetAddress() + "/" +
                        newSocket.getPort() + "\n");
            }
        } catch (IOException e) {
        }
    }

    public void stopServer() {

    }
}
