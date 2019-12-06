import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class MainActivity {
    static ServerSocket serverSocket;
    static Socket socket;
    static Boolean isOpen;
    static int port = 8192;
    public static String incomingText;
    public static String incomingContact;
    static JLabel label;
    static JLabel contactLabel;


    public static void main(String[] args){
        generateGUI();

    }

    static void generateGUI(){
        JFrame frame = new JFrame("Sockets And Salmon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(700,700);

        JPanel panel = new JPanel(); // the panel is not visible in output
        JButton checkNetworkInfo = new JButton("Get Network Info");
        JLabel dataLabel = new JLabel("");
        panel.add(checkNetworkInfo);
        panel.add(dataLabel);

        checkNetworkInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    dataLabel.setText(getIPAddress());
                }catch (Exception exception){
                    exception.printStackTrace();
                }

            }
        });

        JPanel networkPanel = new JPanel();
        JButton socketButton = new JButton("Open The server socket");
        JButton getDataButton = new JButton("Get data from client");
        label = new JLabel("Message:");
        contactLabel = new JLabel("Contact:");

        isOpen = false;

        socketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                label.setText("Listening on port " + port);
                try{
                    startSocket();
                }catch (IOException io) {
                    io.printStackTrace();
                }
            }
        });
        getDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    getData(label);
                }catch (IOException io){
                    io.printStackTrace();
                }
            }
        });

        BackgroundThread listener = new BackgroundThread();
        listener.start();


        networkPanel.add(socketButton);
        networkPanel.add(label);
        networkPanel.add(contactLabel);
        networkPanel.add(getDataButton);

        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, networkPanel);
        frame.setVisible(true);

    }

    public static String getIPAddress() throws Exception{
        return InetAddress.getLocalHost().toString();
    }

    public static ArrayList<NetworkInterface> getNetworkInterfaces() throws Exception{
        return Collections.list(NetworkInterface.getNetworkInterfaces());
    }

    public static void startSocket() throws IOException{
        System.out.println("Starting socket");
        serverSocket = new ServerSocket(8192);
        System.out.println("Server socket initialized");
        socket = serverSocket.accept();
        System.out.println("socket accepted");
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.write("Hello user!\n");
        System.out.println("printwriter done");
        printWriter.flush();
        printWriter.close();
        serverSocket.close();
        socket.close();
    }

    public static void getData(JLabel label) throws IOException{
        System.out.println("Starting socket");
        serverSocket = new ServerSocket(8192);
        System.out.println("Server socket initialized");
        socket = serverSocket.accept();
        System.out.println("socket accepted");
        DataInputStream input = new DataInputStream(socket.getInputStream());
        String inputString;
        while((inputString = input.readLine())!=null){
            label.setText(inputString);
        }
        serverSocket.close();
        socket.close();
    }

    public static void UpdateGUI(String message, String sender){
        label.setText(message);
        contactLabel.setText(sender);
    }


}
