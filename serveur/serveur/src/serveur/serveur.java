package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class serveur {
    private static int totalSum = 0;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
          
            serverSocket = new ServerSocket(12345);
            System.out.println("Le serveur est en attente de connexions...");

            while (true) {
              
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion client.");

                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

             
                clients.add(clientHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final BufferedReader reader;
        private final PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
             
                writer.println("Veuillez entrer votre nom d'utilisateur : ");
                this.username = reader.readLine();
                System.out.println("Authentification réussie pour " + username);

                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        break;
                    }

                    if (message.equals("exit")) {
                        break;
                    }

                    System.out.println("Message reçu de " + username + " : " + message);

                    if (isInteger(message)) {
                        
                        int number = Integer.parseInt(message);
                        totalSum += number;
                        writer.println("Résultat partiel de la somme : " + totalSum);
                        System.out.println("Résultat partiel de la somme envoyé à " + username);

                       
                        broadcastTotalSum();
                    } else {
                        
                        writer.println("Message de " + username + " : " + message);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    System.out.println(username + " déconnecté.");
                    // Retirer le client de la liste
                    clients.remove(this);
                    // Envoi du résultat total mis à jour à tous les clients
                    broadcastTotalSum();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastTotalSum() {
            // Envoi du résultat total à tous les clients
            for (ClientHandler client : clients) {
                client.writer.println("Résultat total de la somme : " + totalSum);
            }
        }

        private boolean isInteger(String str) {
            try {
                Integer.parseInt(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
