import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * Serves Hangman for telnet
 */
public class Server {
	//TODO TCP-Server-Socket and -Clients-Sockets definition

	private static ServerSocket welcomeSocket;
	private static Socket[] sockets;
	
	
	private static BufferedWriter[] writers; // a BufferedWriter for each player
	private static BufferedReader[] readers; // a BufferedReader for each player
	
	private static final int NUM_PLAYERS = 2; // number of players
	private static int curPlayer; // currently active player (0 - n-1)
	
	/*
	 * Initializes the game.
	 * Loops until solution is found or hangman is dead. 
	 * 
	 */
	public static void main (String[] argv) throws Exception {
		//TODO init game and hangman.
		initGame();
		
		Hangman hangy = new Hangman(NUM_PLAYERS);
		
		
		
		while(!hangy.win() || hangy.dead()) //TODO loop until solution is found or hangman is dead.
		{
			//TODO Inform players and read input
			writeToAll(hangy.getHangman());
			writers[curPlayer].write("Your turn!");
			writeToAllButCur("Wait for your turn!");
			String input = readers[curPlayer].readLine();
			
			
			
			
			

			//TODO Process input character and inform players
			if(input.length() == 1) {
				hangy.checkChar(input.charAt(0));
			}
			else {
				hangy.checkWord(input.substring(1));
			}
			writeToAll(hangy.getHangman());
			
			

			//TODO Set curPlayer to next player
			curPlayer = (curPlayer + 1) % NUM_PLAYERS;
			
		}
		
		//TODO Inform players about the game result.
		writeToAll(hangy.getHangman());
	
	
	
	
	
		//TODO Close player sockets.
		for(int i = 0; i<NUM_PLAYERS; i++) {
			sockets[i].close();
		}
	
	
	}
	
	/*
	 * Initializes sockets until number of players NUM_PLAYER is reached
	 */
	private static void initGame() throws Exception {
		//TODO Initialize sockets/arrays and current player.

		writers = new BufferedWriter[NUM_PLAYERS];
		readers = new BufferedReader[NUM_PLAYERS];
		
		curPlayer = 0;
		
		welcomeSocket = new ServerSocket(6789);
		sockets = new Socket[NUM_PLAYERS];
		
		while(curPlayer != NUM_PLAYERS) //TODO Not all players connected
		{
			//TODO Initialize socket and reader/writer for every new connected player.
			
			
			Socket connectSocket = welcomeSocket.accept();
			BufferedReader inFromClient= new BufferedReader(
					new InputStreamReader(connectSocket.getInputStream(),"UTF-8"));
			
			BufferedWriter outToClient= new BufferedWriter(
					new OutputStreamWriter(connectSocket.getOutputStream(), "UTF-8")); 
			
			
			writers[curPlayer] = outToClient;
			readers[curPlayer] = inFromClient;
			sockets[curPlayer] = connectSocket;
			
			
			//TODO Welcome new player and increment current player

			writers[curPlayer].write("Welcome player!");
			writers[curPlayer].flush();
		
			curPlayer += 1;
		
		}
		
		
		//TODO Reset current player.
		curPlayer = 0;

		//TODO Prevent more connections to be established. Inform players about start of the game.
		welcomeSocket.close();
		
		writeToAll("Game is about to start");
	
		
	
	}
	
	/*
	 * Writes s to all players
	 */
	private static void writeToAll (String s) throws Exception {
		//TODO
		for(int i = 0; i < NUM_PLAYERS; i++) {
			writers[i].write(s);
			writers[i].flush();
		}
	}
	
	/*
	 * Writes s to all players but to the current player
	 */
	private static void writeToAllButCur (String s) throws Exception {
		//TODO
		for(int i = 0; i < NUM_PLAYERS; i++) {
			if(i != curPlayer) {
				writers[i].write(s);
				writers[i].flush();
			}
		}

	}
	
}