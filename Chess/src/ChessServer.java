/**
 * Created by Kai W. Fleischman on 2/24/2017.
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;

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
    private ArrayList<Match> matches = new ArrayList<>();
    private ArrayList<HandleAMatch> matchCache = new ArrayList<>();

    public static void main(String[] args)
    {
        new ChessServer();
    }

    private ChessServer() {
        tasks = new ArrayList<>();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            //ip = addr.getHostAddress();
            out.println("IP: " + addr);

            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            out.print("ChessServer server started on " + fixDate("" + new Date()) + '\n');

            // Number a client
            int clientNo = 1;

            boolean endService = false;
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
        private HandleAMatch match;
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

        void sendObject(Object object){
            try{
                objectToClient.writeObject(object);
                objectToClient.reset();
            } catch(Exception e) {
                System.out.println(e);
            }
        }

        private Client getClient() {return client;}

        ObjectOutputStream getObjectOutput(){ return objectToClient;}

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
                    } else if (object instanceof String) {
                        String curr = (String) object;
                        if(client==null) {
                            if (!nameTaken(curr)) {
                                client = new Client(curr);
                                clients.add(client);
                                objectToClient.writeObject(client);
                            } else {
                                objectToClient.writeObject(null);
                            }
                            objectToClient.reset();
                        } else {
                            if(curr.equals("leave"))
                                match.removePerson(this,false);
                            else if(curr.equals("Forfeit"))
                                match.removePerson(this,true);
                        }
                    } else if (object instanceof Match) {
                        Match curr = (Match)object;
                        int index = -1;
                        for(int a = 0; a < matches.size(); a++) {
                            if(matches.get(a).compareTo(curr)==0) {
                                index = a;
                                curr = matches.get(a);
                            }
                        }
                        if(index==-1) {
                            matches.add(curr);
                            HandleAMatch m = new HandleAMatch(this);
                            matchCache.add(m);
                            match = m;
                            writeOutMatches();
                            new Thread(m).start();
                        } else {
                            matchCache.get(index).addPerson(this);
                            if(matches.get(index).getPlayerTwo()==null) {
                                matches.get(index).addPlayerTwo(client);
                                match = matchCache.get(index);
                                writeOutMatches();
                            }
                        }
                    } else if(match!=null) {
                        match.readInput(object);
                    }
                } catch (ClassNotFoundException e) {
                    out.println("Invalid object from Client was received.");
                }
            }
            catch(IOException e) {
                serving = false;
                out.println(e);
                clients.remove(client);
                tasks.remove(this);
                if(match!=null)
                {
                    match.removePerson(this,false);
                    writeOutMatches();
                }
                out.println("Connection lost with <Client: "+client.getName()+"> on " + fixDate("" + new Date()));
            }
        }
    }

    private class HandleAMatch implements Runnable {
        private boolean serving = true;
        private Board setUp = null;
        ArrayList<HandleAClient> clients = new ArrayList<>();
        ArrayList<String> bPLost = new ArrayList<>();
        ArrayList<String> wPLost = new ArrayList<>();

        HandleAMatch(HandleAClient client) {
            String[][] board;
            board = new String[8][8];
            for(int i = 0; i < 64; i++)
            {
                board[i/8][i%8] = "  ";
            }
            board[0][0] = "BR"; board[0][1] = "BN";
            board[0][2] = "BB"; board[0][3] = "BQ";
            board[0][4] = "BK"; board[0][5] = "BB";
            board[0][6] = "BN"; board[0][7] = "BR";
            for(int i = 0; i < 8; i++)
                board[1][i] = "BP";
            for(int i = 0; i < 8; i++)
                board[6][i] = "WP";
            board[7][0] = "WR"; board[7][1] = "WN";
            board[7][2] = "WB"; board[7][3] = "WQ";
            board[7][4] = "WK"; board[7][5] = "WB";
            board[7][6] = "WN"; board[7][7] = "WR";
            setUp = new Board(board);
            clients.add(client);
            client.sendObject(setUp);
        }
        void readInput(Object object)
        {
            if(object instanceof Board)
            {
                Board curr = (Board)object;
                setUp.setBoard(curr.getBoard());
                sendBoard(curr);
            }
            else if(object instanceof ArrayList)
            {
                ArrayList curr = (ArrayList) object;
                if(curr.size()>0)
                {
                    if(curr.get(0) instanceof String)
                    {
                        ArrayList<String> arr = (ArrayList<String>) object;
                        if(arr.get(0).contains("W"))
                            wPLost = arr;
                        else
                            bPLost = arr;
                        sendLists();
                    }
                }
            }
        }
        void removePerson(HandleAClient client, boolean forfeit)
        {
            clients.remove(clients.indexOf(client));
            if(clients.size()==0)
            {
                int index = matchCache.indexOf(this);
                matches.remove(index);
                matchCache.remove(this);
                writeOutMatches();
                serving = false;
            }
            if(forfeit)
                forfeit(client.getClient().getName());
        }

        private void forfeit(String name)
        {
            for (HandleAClient client : clients) {
                client.sendObject("Game Over, "+name);
            }
        }

        private void sendBoard(Board m)
        {
            for (HandleAClient client : clients) {
                client.sendObject(m);
            }
        }

        private void sendLists()
        {
            for (HandleAClient client : clients) {
                client.sendObject(bPLost);
                client.sendObject(wPLost);
            }
        }

        private void addPerson(HandleAClient client) {
            clients.add(client);
            client.sendObject(setUp);
            client.sendObject(bPLost);
            client.sendObject(wPLost);
        }
        public void run() {
            while(true){
                if (!(serving)) break;
            }
        }
    }
}