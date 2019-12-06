import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class BackgroundThread extends Thread {
    ServerSocket serverSocket;
    Socket socket;

    public BackgroundThread(){
        super("Background Thread");

    }

    public void run(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                receiveData();
                System.out.println("Finished the REceive!!!!@!!!!!?/");
            }
        }, 0, 100);
    }

    public void receiveData(){
        System.out.println("Running the REceive Data ------------------");
        try{
            serverSocket = new ServerSocket(8192);
            socket = serverSocket.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            String inputString;
            while((inputString = input.readLine())!=null){
                JSONObject jObject = new JSONObject(inputString);
                MainActivity.UpdateGUI(jObject.getString("text"), jObject.getString("sender"));
            }
            serverSocket.close();
            socket.close();
        }catch (IOException io){
            io.printStackTrace();
        }
    }

}