package com.learning.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {
    private static final int PORT = 8888;
    private JTextArea serverTextArea = new JTextArea();
    private JPanel buttonTool = new JPanel();
    private JButton startButton = new JButton("Launch");
    private JButton stopButton = new JButton("Stop");

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream dis = null;

    public static void main(String[] args) {
        Server server = new Server();
    }

    public Server() throws HeadlessException {
        this.setTitle("Server log");
        this.add(serverTextArea, BorderLayout.CENTER);
        buttonTool.add(startButton);
        buttonTool.add(stopButton);
        this.add(buttonTool, BorderLayout.SOUTH);

        this.setBounds(0, 0, 500, 500);
        serverTextArea.append("Server hasn't launched yet. Please launch the server.\n");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Server should shutdown when closed window -- need improvement
        serverTextArea.setEditable(false);
        this.setVisible(true);
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("The server has launched");
            socket = serverSocket.accept();
            System.out.println("A client has connected to the server: " + socket.getInetAddress() + "/" + socket.getPort());
            serverTextArea.append("A client has connected to the server: " + socket.getInetAddress() + "/" +
                    socket.getPort() + "\n");

            receiveMessage();
        } catch (IOException e) {
        }
    }

    public void receiveMessage() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            String message = dis.readUTF();
            System.out.println(message);
            serverTextArea.append(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
