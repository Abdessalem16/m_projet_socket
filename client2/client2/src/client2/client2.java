package client2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client2 {
    public static void main(String[] args) {
        try {
           
            Socket socket = new Socket("localhost", 12345);
            System.out.println("Connecté au serveur.");

           
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            
            System.out.print("Veuillez entrer votre nom d'utilisateur : ");
            String username = reader.readLine();
            System.out.println("Authentification réussie pour " + username);

            
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = reader.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                writer.println(userInput);
                if (userInput.equals("exit")) {
                    break;
                }
            }

        
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
