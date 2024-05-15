package server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class server {
    public static void main(String[] args) {
        // Informații pentru conexiunea la baza de date MySQL
        String url = "jdbc:mysql://localhost:3306/wallet?useSSL=false"; // Adresa și numele bazei de date
        String username1 = "root"; // Numele utilizatorului
        String password1 = "elenadaniela16"; // Parola utilizatorului

        try {
            // Conectare la baza de date
            Connection connection = DriverManager.getConnection(url, username1, password1);
            System.out.println("Conectat la baza de date!");

            // Cream un server socket care asculta pe portul 8080
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Serverul asculta pe portul 8080...");

            while (true) {
                // Acceptăm conexiunea de la client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Clientul s-a conectat: " + clientSocket);

                // Cream un cititor și un scriitor pentru comunicarea cu clientul
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Citim mesajul de la client și îl afișăm
                String message = in.readLine();
                System.out.println("Mesaj primit de la client: " + message);

                // Parsăm mesajul primit de la client
                String[] data = message.split("\\|");
                if (data.length == 4 && data[0].equals("CREATE_ACCOUNT")) {
                    String email = data[1];
                    String username = data[2];
                    String password = data[3];

                    // Adăugăm datele în baza de date
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO utilizatori (Username, Parola, Email) VALUES (?, ?, ?)");
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    preparedStatement.setString(3, email);
                    preparedStatement.executeUpdate();

                    // Trimitem un răspuns către client pentru a confirma succesul
                    out.println("Cont creat cu succes!");
                } else {
                    // Trimitem un mesaj de eroare către client
                    out.println("Eroare: Cerere invalidă!");
                }

                // Închidem conexiunea cu clientul
                clientSocket.close();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}