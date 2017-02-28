/**
 * Created by Kai W. Fleischman on 2/24/2017.
 */

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.*;

public class ChessServer
{
    /*
    BufferedWriter out = null;
    boolean saveHistory = false;
    */
    private ArrayList<HandleAClient> tasks;
    private ArrayList<Message> messages = new ArrayList<>();
    private ArrayList<Client> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private ArrayList<Match> matches = new ArrayList<>();
    private boolean endService = false;
    public static void main(String[] args)
    {
        new ChessServer();
    }

    private ChessServer() {
        tasks = new ArrayList<HandleAClient>();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            //ip = addr.getHostAddress();
            out.println("IP: " + addr);

            // Create a server socket
            serverSocket = new ServerSocket(8000);
            out.print("ChessServer server started on " + fixDate("" + new Date()) + '\n');

            // Number a client
            int clientNo = 1;

            while (!endService) {
                Socket socket = serverSocket.accept();
                out.print("Starting thread for client " + clientNo + " on " + fixDate("" + new Date()) + '\n');
                InetAddress inetAddress = socket.getInetAddress();
                out.print("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
                HandleAClient task = new HandleAClient(socket);
                tasks.add(task);
                new Thread(task).start();
                clientNo++;
            }
        }
        catch(IOException ex) {
            out.println("An error occur.");
        }
    }

    private String fixDate(String date)
    {
        String[] dateComponents = date.split(" ");
        String year = dateComponents[5].substring(2,4);
        String day = dateComponents[0];
        String month = dateComponents[1];
        String numberDay = dateComponents[2];
        String time = dateComponents[3];
        String amPM;

        if(numberDay.charAt(0) == '0')
            numberDay = numberDay.substring(1,2);
        int hour = Integer.parseInt(time.substring(0,2));
        if(hour > 12)
        {
            amPM = "PM";
            hour -= 12;
        }
        else
            amPM = "AM";

        switch( month )
        {
            case "Jan": month = "1"; break;
            case "Feb": month = "2"; break;
            case "Mar": month = "3"; break;
            case "Apr": month = "4"; break;
            case "May": month = "5"; break;
            case "Jun": month = "6"; break;
            case "Jul": month = "7"; break;
            case "Aug": month = "8"; break;
            case "Sep": month = "9"; break;
            case "Oct": month = "10"; break;
            case "Nov": month = "11"; break;
            case "Dec": month = "12"; break;
            default: month = "13";
        }
        day = day.substring(0,3);
        switch( day )
        {
            case "Mon": day = "Monday"; break;
            case "Tue": day = "Tuesday"; break;
            case "Wed": day = "Wednesday"; break;
            case "Thu": day = "Thursday"; break;
            case "Fri": day = "Friday"; break;
            case "Sat": day = "Saturday"; break;
            case "Sun": day = "Sunday"; break;
            default: day = "No day"; break;
        }

        return day + ", " + month + "/" + numberDay + "/" + year + " at "
                + hour + time.substring(2,time.length()) + " " + amPM;
    }

    private boolean nameTaken(String name)
    {
        boolean taken = false;
        for(Client a : clients)
        {
            if(a.getName().equals(name))
                taken = true;
        }
        return taken;
    }



    /*public void stopServer()
    {
        endService = true;
        try{serverSocket.close();
            exit(0);}
        catch(IOException e){out.println("Could not stop server.");}
    }*/

    private void writeOutMessages()
    {
        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
                try{
                    objectToClient = task.getObjectOutput();
                    objectToClient.reset();
                    objectToClient.writeObject(messages);
                    objectToClient.reset();
                }catch(IOException e){System.out.println("Error");}
        }
    }

    private void writeOutMatches()
    {
        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
            try{
                objectToClient = task.getObjectOutput();
                objectToClient.reset();
                objectToClient.writeObject(matches);
                objectToClient.reset();
            }catch(IOException e){System.out.println("Error");}
        }
    }

    // Inner class
    // Define the thread class for handling new connection
    private class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        boolean serving = true;
        ObjectOutputStream objectToClient;
        ObjectInputStream objectFromClient;
        Client client = null;

        /** Construct a thread */
        HandleAClient(Socket sock) {
            socket = sock;
            try{
                objectToClient = new ObjectOutputStream(socket.getOutputStream());
                objectFromClient = new ObjectInputStream(socket.getInputStream());
            }catch(IOException e){out.println("Could not connect to Client.");}
        }

        public ObjectOutputStream getObjectOutput() {return objectToClient;}

        /*public String getUsername()
        {
            return userName;
        }*/

        /** Run a thread */
        public void run() {
            try {
                // Create data input and output streams
                // Continuously serve the client
                while (serving) try {
                    Object object = objectFromClient.readObject();
                    if (object instanceof Message) {
                        Message temp = (Message)object;
                        messages.add(0,temp);
                        System.out.println("Received <Message: "+temp.getMessage()+"> from <Client: "
                                +client.getName()+"> on " + fixDate("" + new Date()));
                        writeOutMessages();
                    } else if (object instanceof Integer) {
                        Integer curr = (Integer) object;
                        if(curr==2 && messages.size()>0){
                            objectToClient.writeObject(messages);
                            objectToClient.reset();
                        } else if (curr==3 && matches.size()>0){
                            objectToClient.writeObject(matches);
                            objectToClient.reset();
                        }
                        System.out.println(curr);
                    } else if (object instanceof String) {
                        String curr = (String) object;
                        if (!nameTaken(curr)) {
                            client = new Client(curr);
                            clients.add(client);
                            objectToClient.writeObject(client);
                        } else {
                            objectToClient.writeObject(null);
                        }
                        objectToClient.reset();
                    } else if (object instanceof Match)
                    {
                        Match curr = (Match)object;
                        matches.add(curr);
                        writeOutMatches();
                    }
                } catch (ClassNotFoundException e) {
                    out.println("Invalid object from Client was received.");
                }
            }
            catch(IOException e) {
                clients.remove(client);
                tasks.remove(this);
                out.println("Connection lost with <Client: "+client.getName()+"> on " + fixDate("" + new Date()));
            }

        }
    }
}
