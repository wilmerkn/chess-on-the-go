import client.Client;
import server.Server;

/**
 * Test2Clients: Starts a server on local host as well as 2 clients so that the user can play with 2 boards on the same computer
 * @version 1.0
 * @author wilmerknutas
 */
public class Test2Clients {
    public static void main(String[] args) {
        Server server = new Server();
        Client client1 = new Client();
        Client client2 = new Client();
    }
}