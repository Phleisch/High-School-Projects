/**
 * Created by Kai W. Fleischman on 2/21/2017.
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ChappServer
{
    BufferedWriter out = null;
    boolean saveHistory = false;
    ArrayList<ChatRoom> chatRooms;
    ArrayList<Message> messages;
    ArrayList<User> userCache;
    ArrayList<HandleAClient> tasks;
    ServerSocket serverSocket;
    public static void main(String[] args)
    {
        new ChappServer();
    }

    public ChappServer() {

        userCache = new ArrayList<User>();
        messages = new ArrayList<Message>();
        chatRooms = new ArrayList<ChatRoom>();
        tasks = new ArrayList<HandleAClient>();

        for(int n = 1; n < 4; n++)
            chatRooms.add(new ChatRoom("Chat Room "+n));
        chatRooms.add(new ChatRoom("Limbo"));

        Scanner in = new Scanner(System.in);
        System.out.print("Save History?: ");
        String answer = in.nextLine();
        if(answer.equalsIgnoreCase("Y"))
            saveHistory = true;
        System.out.println(saveHistory);

        try {
            if(saveHistory)
                out = new BufferedWriter(new FileWriter("M.txt"));
            InetAddress addr = InetAddress.getLocalHost();
            //ip = addr.getHostAddress();
            System.out.println("IP: " + addr);

            // Create a server socket
            serverSocket = new ServerSocket(8000);
            System.out.print("BasedC222 server started on " + fixDate("" + new Date()) + '\n');

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

    public boolean validUser(User user)
    {

        for(User current : userCache)
            if(user.getName().equalsIgnoreCase(current.getName()))
                return false;
        return true;

    }

    public void leaveChatRoom(User user)
    {
        String chatRoom = user.getDesignation();
        chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
        int number = Integer.parseInt(chatRoom);
        number = number - 1;
        chatRooms.get(number).removeUser(user);
    }

    public void addMessageToChat(Message m, User user)
    {
        if(saveHistory)
        {
            try
            {
                out.write(m.getMessage());
            }catch(IOException e){}
        }

        if(user.getName().equals("Admin's Version"))
        {
            chatRooms.get(0).addMessage(m);
            chatRooms.get(1).addMessage(m);
            chatRooms.get(2).addMessage(m);
        }

        else{
            String chatRoom = user.getDesignation();
            chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
            int number = Integer.parseInt(chatRoom);
            number = number - 1;
            chatRooms.get(number).addMessage(m);
        }
        messages.add(0,m);


    }

    public void leftMessage(User user)
    {
        String chatRoom = user.getDesignation();
        chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
        int number = Integer.parseInt(chatRoom);
        number = number - 1;
        Message m = new Message("*"+user.getName()+" left this Chat*",null,user,"Notice");
        chatRooms.get(number).addMessage(m);
        messages.add(0,m);
    }

    public void joinedMessage(User user)
    {
        String chatRoom = user.getDesignation();
        chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
        int number = Integer.parseInt(chatRoom);
        number = number - 1;
        Message m = new Message("*"+user.getName()+" joined this Chat*",null,user,"Notice");
        chatRooms.get(number).addMessage(m);
        messages.add(0,m);
    }

    public boolean kick(String userName)
    {
        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
            if(task.getUsername().equals(userName))
            {
                objectToClient = task.getObjectOutput();
                try{objectToClient.close();}
                catch(IOException e){}
                return true;

            }
        }
        return false;
    }

    public boolean mute(String userName)
    {
        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
            if(task.getUsername().equals(userName))
            {
                objectToClient = task.getObjectOutput();
                try{Message m = new Message("*You were muted*",null,null,"Notice");
                    objectToClient.writeObject(m);}
                catch(IOException e){}
                return true;

            }
        }
        return false;
    }

    public boolean unmute(String userName)
    {
        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
            if(task.getUsername().equals(userName))
            {
                objectToClient = task.getObjectOutput();
                try{Message m = new Message("*You were unmuted*",null,null,"Notice");
                    objectToClient.writeObject(m);}
                catch(IOException e){}
                return true;

            }
        }
        return false;
    }

    public void writeOutMessages(User u)
    {

        ObjectOutputStream objectToClient;
        for(HandleAClient task : tasks)
        {
            //	System.out.println(task.getLocation());
            //	System.out.println(u.getDesignation());
            if(u.getDesignation().equals(task.getLocation()) && !task.getLocation().equals("Universal") && !task.getLocation().equals(""))
            {
                //System.out.println(task.getLocation() + "Line 222");
                try{
                    objectToClient = task.getObjectOutput();
                    String chatRoom = task.getLocation();
                    chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
                    int number = Integer.parseInt(chatRoom);
                    number = number - 1;
                    objectToClient.reset();
                    objectToClient.writeObject(chatRooms.get(number).getMessages());
                    objectToClient.reset();
                }catch(IOException e){System.out.println("Error");}
            }
            else if(!task.getLocation().equals("Universal") && !task.getLocation().equals(""))
            {

                try{
                    objectToClient = task.getObjectOutput();
                    String chatRoom = task.getLocation();
                    chatRoom = chatRoom.substring(chatRoom.length()-1,chatRoom.length());
                    int number = Integer.parseInt(chatRoom);
                    number = number - 1;
                    objectToClient.reset();
                    objectToClient.writeObject(chatRooms.get(number).getMessages());
                    objectToClient.reset();
                }catch(IOException e){System.out.println("Error");}
            }
            else if(task.getLocation().equals("Universal"))
            {
                try{objectToClient = task.getObjectOutput();
                    objectToClient.reset();
                    objectToClient.writeObject(messages);
                }catch(IOException e){System.out.println("Error");}
            }

        }

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
            User user = null;
            //private messageSentListener listener;

            try {
                // Create data input and output streams
                User temp;
                Designation = "";
                // Continuously serve the client
                while (true) {
                    try{Object object = objectFromClient.readObject();
                        if(object instanceof User)
                        {
                            temp = (User) object;
                            if(validUser(temp))
                            {
                                chatRooms.get(3).addUser(temp);
                                user = temp;
                                userName = user.getName();
                                userCache.add(user);
                                System.out.println("Received valid User object from IP " + user.getIP() + " on " + fixDate("" + new Date()));
                                objectToClient.writeObject(user);
                                if(userName.equals("Admin's Version"))
                                {
                                    objectToClient.writeObject(messages);
                                    chatRooms.get(0).addUser(user);
                                    chatRooms.get(1).addUser(user);
                                    chatRooms.get(2).addUser(user);
                                }

                            }
                            else
                            {
                                System.out.println("Received invalid User object from IP " + temp.getIP() + " on " + fixDate("" + new Date()));
                                objectToClient.writeObject(null);
                            }

                        }
                        else if(object instanceof Message)
                        {
                            Message tempMessage = (Message) object;
                            if(tempMessage.getMessage().equals("/protocolG11D4"))
                                stopServer();
                            else if(tempMessage.getMessage().indexOf("/kick.") != -1 && user.getName().equals("Admin's Version"))
                            {
                                kick(tempMessage.getMessage().substring(6));
                            }
                            else if(tempMessage.getMessage().indexOf("/mute.") != -1 && user.getName().equals("Admin's Version"))
                            {
                                mute(tempMessage.getMessage().substring(6));
                            }
                            else if(tempMessage.getMessage().indexOf("/unmute.") != -1 && user.getName().equals("Admin's Version"))
                            {
                                unmute(tempMessage.getMessage().substring(8));
                            }
                            else if(tempMessage.getMessage().equals("Client Terminated"))
                            {
                                System.out.println(user.getIP()+" disconnected.");
                                for(HandleAClient task : tasks)
                                {
                                    if(task.getUsername().equals(getUsername()))
                                    {
                                        tasks.remove(task);
                                        break;
                                    }
                                }
                                objectToClient.close();
                                objectFromClient.close();
                            }
                            else
                            {
                                addMessageToChat(tempMessage,user);
                                objectToClient.reset();
                                writeOutMessages(user);
                            }
                            System.out.println("Received message \"" + tempMessage.getMessage() + "\" on " + fixDate("" + new Date()));
                        }
                        else if(object instanceof String)
                        {
                            String tempString = (String) object;
                            switch( tempString )
                            {
                                case "ChatRoom1": user.setDesignation("ChatRoom1"); chatRooms.get(0).addUser(user);
                                    System.out.println("User added to Chat Room 1"); Designation = "ChatRoom1";
                                    joinedMessage(user); writeOutMessages(user); break;
                                case "ChatRoom2": user.setDesignation("ChatRoom2"); chatRooms.get(1).addUser(user);
                                    System.out.println("User added to Chat Room 2"); Designation = "ChatRoom2";
                                    joinedMessage(user); writeOutMessages(user); break;
                                case "ChatRoom3": user.setDesignation("ChatRoom3"); chatRooms.get(2).addUser(user);
                                    System.out.println("User added to Chat Room 3"); Designation = "ChatRoom3";
                                    joinedMessage(user); writeOutMessages(user); break;
                                case "Left": leftMessage(user); leaveChatRoom(user); user.setDesignation("");
                                    System.out.println("User left "+Designation); Designation = "";
                                    writeOutMessages(user); break;
                                case "Universal": user.setDesignation("Universal"); Designation = "Universal"; break;
                                case "UniversalLeft": user.setDesignation(""); Designation = ""; break;
                                default: /*nothing*/;
                            }

                            switch( user.getDesignation() )
                            {
                                case "ChatRoom1": try{
                                    objectToClient.writeObject(chatRooms.get(0).getMessages());
                                    objectToClient.reset();
                                }catch(IOException e){System.out.println("Error");};
                                    break;
                                case "ChatRoom2": try{
                                    objectToClient.writeObject(chatRooms.get(1).getMessages());
                                    objectToClient.reset();
                                }catch(IOException e){System.out.println("Error");};
                                    break;
                                case "ChatRoom3": try{
                                    objectToClient.writeObject(chatRooms.get(2).getMessages());
                                    objectToClient.reset();
                                }catch(IOException e){System.out.println("Error");};
                                case "Universal": try{
                                    objectToClient.writeObject(messages);
                                    objectToClient.reset();
                                }catch(IOException e){System.out.println("Error");};
                                    break;

                                default: /*nothing*/;
                            }

                        }
                    }

                    catch(ClassNotFoundException e){}

                }
            }
            catch(IOException e) {
                if(userCache.indexOf(user) != -1)
                {
                    System.out.println("Connection with " + user.getIP() + " lost on " + fixDate("" + new Date()));
                    if(!Designation.equals("Universal") && !Designation.equals(""))
                        leaveChatRoom(user);
                    userCache.remove(userCache.indexOf(user));

                }
                else
                    System.out.println("Connection lost with a client on " + fixDate("" + new Date()));
            }

        }
    }
}