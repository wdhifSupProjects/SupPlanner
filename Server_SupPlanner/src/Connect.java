import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;

public class Connect {
    public static void main(String[] args){
        Connection connexion = null;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver OK");
            String url = "jdbc:mysql://localhost/SupPlanner";
            String user = "root";
            String passwd = "";

            connexion = DriverManager.getConnection(url, user,passwd);
            System.out.println("Connexion effective !");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PrintWriter out;
        BufferedReader Read;
        ServerSocket socketServer;
        Socket socketDuServeur ;
        Scanner sc = new Scanner(System.in);

        try
        {
            while (true)
            {
                socketServer = new ServerSocket(8080);
                System.out.println("Le serveur est à l'écoute du port "+socketServer.getLocalPort());
                socketDuServeur = socketServer.accept();
                System.out.println("Tentative de connection");
                Read = new BufferedReader (new InputStreamReader(socketDuServeur.getInputStream()));
                String step = Read.readLine();
                if (step.equals("login"))
                {
                    String log = Read.readLine();
                    String password = Read.readLine();
                    out = new PrintWriter(socketDuServeur.getOutputStream());
                    Boolean connectionTry;
                    if (log.equals("") || password.equals(""))
                    {
                        connectionTry = false;
                    }
                    else
                    {
                        connectionTry = Login.login(connexion, log, password);
                    }

                    if (connectionTry)
                    {
                        System.out.println("Connection établie.");
                        out.println("true");
                        out.flush();
                    }
                    else
                    {
                        out.println("false");
                        out.flush();
                    }
                    socketServer.close();
                    socketDuServeur.close();
                }
                else if(step.equals("register"))
                {
                    String name = Read.readLine();
                    String mail = Read.readLine();
                    String pass = Read.readLine();
                    String userType = Read.readLine();
                    out = new PrintWriter(socketDuServeur.getOutputStream());
                    Boolean inscriptionTry = true;
                    boolean checkN = Check.checkName(connexion, name);
                    boolean checkM = Check.checkMail(connexion, mail);
                    if (!checkN && !checkM)
                    {
                        inscriptionTry = Register.register(connexion, name, mail, pass, userType);
                        out.println("RAS");
                    }
                    else
                    {
                        if (checkN)
                        {
                            System.out.println("Impossible de créer utilisateur le nom est déja utilisé.");
                            out.println("NameU");
                            out.flush();
                        }
                        else if (checkM)
                        {
                            System.out.println("Impossible de créer utilisateur le mail est déja utilisé.");
                            out.println("MailU");
                            out.flush();
                        }
                        inscriptionTry = false;
                    }

                    if (inscriptionTry)
                    {
                        System.out.println("Ajout utilisateur effectué.");
                        out.println("true");
                        out.flush();
                    }
                    else
                    {
                        out.println("false");
                        out.flush();
                    }
                    socketServer.close();
                    socketDuServeur.close();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            connexion.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
