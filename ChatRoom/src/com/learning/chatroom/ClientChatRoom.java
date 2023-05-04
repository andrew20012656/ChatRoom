package com.learning.chatroom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientChatRoom extends JFrame {
    private static final String CONNECTION = "127.0.0.1";
    private static final int CONNECTION_PORT = 8888;
    private JTextArea textArea = new JTextArea(10, 20);
    private JScrollPane scrollPane = new JScrollPane(textArea);
    private JTextField textField = new JTextField(20);
    private Socket socket = null;
    private boolean isConnected = false;
    private DataOutputStream dos = null;

    public static void main(String[] args) {
        ClientChatRoom cc1 = new ClientChatRoom();
        cc1.init();
    }

    public ClientChatRoom() throws HeadlessException {
        super();
    }

    public void init() {
        this.setTitle("Client Chatroom");
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(textField, BorderLayout.SOUTH);
        this.setBounds(300, 300, 300, 400);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageToSend = textField.getText();
                if(messageToSend.trim().length() == 0) {
                    return;
                }
                send(messageToSend);
                textField.setText("");
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when clicked on exit
        textArea.setEditable(false);
        textField.requestFocus(); // The cursor is placed in textField instead of textArea

        try {
            socket = new Socket(CONNECTION, CONNECTION_PORT);
            isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);

        new Thread(new ReceiveMessage()).start();
    }

    //
    public void send(String message) {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReceiveMessage implements Runnable {
        @Override
        public void run() {
            while(isConnected) {
                try {
                    DataInputStream dataIn = new DataInputStream(socket.getInputStream());
                    String messageFromServer = dataIn.readUTF();
                    textArea.append(messageFromServer + "\n");
                } catch (IOException e) {
                    System.out.println("The server is disconnected.");
                    textArea.append("The server is disconnected.\n");
                    isConnected = false;
                }
            }
        }
    }

}
