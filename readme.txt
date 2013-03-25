/*
 * Juan Antonio Aldea Armenteros (0404450)
 * LUT - Object Oriented Programming Techniques
 *                  2013
 */


ATTENTION:

Because I think I don't understand the hand in notes and I'm not sure if I'm using something not very common for the GUI, I have added a runnable jar file (with all the needed libs embedded and called Jokes0404450Runnable.jar) to the the jar file that contains all the source code, readme and joke file.

Implemented features:

- The server has a text file containing one joke per line
    - Implemented, this file is called Jokes0404450.txt, it is updated when the server is disabled.

- The client establishes a connection to the server over the network, and the server sends a random joke to the client as a String, which the client then outputs to the user
    - Implemented using SocketChannels since I wanted to use select() for solving the multiclient part.
    - A soon as the client connects the server sends a joke to it.

- In addition your server must support multiple simultaneous connections
    - Implemented. The server keeps a list of clients and checks their status by using select()
- Server asks the client after each joke if the client wants to see another joke and server must also keep sending jokes as long as the answer is favorable
    -Implemented, but not exactly in that way, server doesn't ask, it doesn't make much sense, is the client who requests or no another joke.
    -For requesting another joke just press Request Joke button

- Client stores each different joke it receives (meaning that no duplicates are stored) and write the jokes to the file (one joke per line). This file can be accessed after we don't want to receive any more jokes from the server
    - This file is called JokesClient0404450.txt
    - The file is updated in real time

- Control mechanisms for server to:
    - stop serving clients
        - Server can be completely stopped by clicking Stop Server
        - Server can reject new connections by clicking on the checkbox
    - start serving clients
        -Server can be started by pressing Start server
        -Server can stop rejecting new connection by clicking the checkbox
    - get a new joke from the command line and adding it to the file
        - Implemented, write the new joke in the server's textbook and click on save.
     
A graphical user interface (GUI) for client (not necessary for server)
    - Both GUI are implemented as tabs.

General notes
    -Server and Client have their own thread, outside the GUI thread.
    -Communication between server and client threads and GUI is done by using a monitor.
    -Some asynchronous requests are also used for updating the GUI (those updates coming from server/client thread)
    -Server and client role are mutually exclusive, but this limitation is only imposed by the GUI, it is an arbitrary decision, both should live in harmony together
