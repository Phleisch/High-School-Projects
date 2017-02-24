/**
 * Created by Kai W. Fleischman on 2/24/2017.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ChessServer
{
    BufferedWriter out = null;
    boolean saveHistory = false;
    private ArrayList<HandleAClient> tasks;

    ServerSocket serverSocket;
    public static void main(String[] args)
    {
        new ChessServer();
    }

    public ChessServer() {
        tasks = new ArrayList<HandleAClient>();






        Scanner in = new Scanner(System.in);



        try {


            InetAddress addr = InetAddress.getLocalHost();
            //ip = addr.getHostAddress();
            System.out.println("IP: " + addr);

            // Create a server socket
            serverSocket = new ServerSocket(8000);
            System.out.print("ChessServer server started on " + fixDate("" + new Date()) + '\n');

            // Number a client
            int clientNo = 1;

            while (true) {
                // Listen for a new connection request
                Socket socket = serverSocket.accept();

                // Display the client number
                System.out.print("Starting thread for client " + clientNo + " on " + fixDate("" + new Date()) + '\n');

                // Find the client's host name, and IP address
                InetAddress inetAddress = socket.getInetAddress();
                System.out.print("Client " + clientNo + "'s host name is " + inetAddress.getHostName() + "\n");
                System.out.print("Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");

                // Create a new thread for the connection
                HandleAClient task = new HandleAClient(socket);
                tasks.add(task);

                // Start the new thread
                new Thread(task).start();

                // Increment clientNo
                clientNo++;
            }
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }

    public String fixDate(String date)
    {
        String[] dateComponents = date.split(" ");
        String year = dateComponents[5].substring(2,4);
        String day = dateComponents[0];
        String month = dateComponents[1];
        String numberDay = dateComponents[2];
        String time = dateComponents[3];
        String amPM ="";

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
            default: day = "Noday"; break;
        }

        String newDate = day + ", " + month + "/" + numberDay + "/" + year + " at "
                + hour + time.substring(2,time.length()) + " " + amPM;

        return newDate;
    }

    public void stopServer()
    {

        try{serverSocket.close();
            System.exit(0);}
        catch(IOException e){}
    }

    // Inner class
    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        ObjectOutputStream objectToClient;
        ObjectInputStream objectFromClient;
        String Designation;
        String userName;

        /** Construct a thread */
        public HandleAClient(Socket socket) {
            this.socket = socket;
            try{
                objectToClient = new ObjectOutputStream(socket.getOutputStream());
                objectFromClient = new ObjectInputStream(socket.getInputStream());
            }catch(IOException e){}
        }


        public ObjectOutputStream getObjectOutput()
        {
            return objectToClient;
        }

        public String getLocation()
        {
            return Designation;
        }

        public String getUsername()
        {
            return userName;
        }

        /** Run a thread */
        public void run() {
            try {
                // Create data input and output streams
                Designation = "";
                // Continuously serve the client
                while (true) {
                    try{Object object = objectFromClient.readObject();
                        if(object instanceof Integer)
                        {

                        }
                        else if(object instanceof Double)
                        {

                        }
                        else if(object instanceof String)
                        {

                        }
                    }

                    catch(ClassNotFoundException e){}

                }
            }
            catch(IOException e) {
                /*
                if(userCache.indexOf(user) != -1)
                {
                }
                else
                */
                System.out.println("Connection lost with a client on " + fixDate("" + new Date()));
            }

        }
    }
}
