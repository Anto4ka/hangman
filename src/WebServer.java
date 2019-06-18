import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

/*
 * Serves Hangman for HTTP
 */
public final class WebServer {
	//TODO TCP-Server-Socket definition
	

	private static final String EOL = "<CR><LF>";

	private static int session; // session id
	private static int sessionCookie; // session id cookie of the currently handled request
	private static int playerCookie; // player number of the currently handled request
	private static Hangman hangman; // Hangman game object
	private static String prevMsg; // used to save the result message of the previous action
	
	private static final int NUM_PLAYERS = 2; // number of players
	private static int curPlayer; // currently active player (0 - n-1)
	
	private static boolean gameStarted = false; // indicates whether we are already in-game or still waiting for some players
	private static boolean gameEnded = false; // indicates whether the game has ended

	/*
	 * Initializes the game.
	 * Loops until solution is found or hangman is dead.
	 */
	public static void main (String[] argv) throws Exception {
		//TODO Initialize socket, global variables and hangman.

		ServerSocket welcomeSocket = new ServerSocket(6789);
		hangman = new Hangman(NUM_PLAYERS);
		curPlayer = 0;
		
		while(true) {
			//TODO Accept client request
			
			Socket connectSocket = welcomeSocket.accept();
			BufferedReader br= new BufferedReader(
					new InputStreamReader(connectSocket.getInputStream(),"UTF-8"));
			
			BufferedWriter bw= new BufferedWriter(
					new OutputStreamWriter(connectSocket.getOutputStream(), "UTF-8")); 
			
			
			if (!gameStarted && !gameEnded) {
				processInitRequest(br, bw);
			} else if (gameStarted && !gameEnded) {
				processGameRequest(br, bw);
			} else {
				processEndRequest(br, bw);
				if (curPlayer == NUM_PLAYERS) break;
			}
			
		}

	}

	/*
	 * Handles HTTP conversation when game has not yet started
	 * Waits for number of players NUM_PLAYERS to be present
	 */
	private static void processInitRequest(BufferedReader br, BufferedWriter bw) throws Exception {
		//TODO Process request and header lines.
		
		String request = br.readLine();
		String header = "";
		String trash = "";
		
		while(header != "<CR><LF>") {
			request += header + "\n";
			header = br.readLine();
		}
		while(trash != "<CR><LF>") {
			trash = br.readLine();
		}
		
		
		
		String content = "<HTML><HEAD><META http-equiv=\"refresh\" content=\"2\"><TITLE>Hangman</TITLE></HEAD><BODY>" +
				"Willkommen zu I7Hangman!<BR>Du bist Spieler ";
		//TODO If the player is unknown: send cookies and increment curPlayer. Add player number to content.

		if(!header.contains("Cookie")) {
			bw.write("Set-Cookie:name=" + curPlayer);
			bw.flush();
			curPlayer = (curPlayer + 1) % NUM_PLAYERS;
		}
		
		content += 1;
		
		
		
		content += ".<BR>Es darf reihum ein Buchstabe geraten werden.<BR>Die Seite lädt automatisch neu.<BR>" +
				"Warte auf alle Spieler...</BODY></HTML>";

		//TODO send response to player
		bw.write(content+ EOL);
		bw.flush();
		
		if (curPlayer == NUM_PLAYERS) {
			gameStarted = true;
			curPlayer = 0;
		}
	}

	/*
	 * Handles HTTP conversation when game is running.
	 * Differentiates between current player and other players.
	 */
	private static void processGameRequest(BufferedReader br, BufferedWriter bw) throws Exception {
		//TODO Process request and header lines.

		String request = br.readLine();
		String header = "";
		String trash = "";
		
		while(header != "<CR><LF>") {
			request += header + "\n";
			header = br.readLine();
		}
		while(trash != "<CR><LF>") {
			trash = br.readLine();
		}
		
		String guess = "";
		
		// get Cookie
		String[] parts = header.split("\n");
		int cookie = 0;
		for(int i = 0; i<parts.length; i++) {
			if(parts[i].contains("Cookie")) {
				cookie = Integer.parseInt(parts[i].split("=")[1].replace(" ", ""));
			}
		}
		
		// Construct the response message.
		String content = "<HTML><HEAD><TITLE>Hangman</TITLE>";
		
		if(cookie == curPlayer ) //TODO player is current player and form was submitted
		{
			if(guess.length() == 1) //TODO handle single character guess
			{
				hangman.checkCharHtml(guess.charAt(0));
				
			
			} else if(guess.charAt(0)=="!".charAt(0)) //TODO handle word guess
			{
				hangman.checkWordHtml(guess.substring(1));
				
			
			}
			//TODO Set curPlayer to next player
			curPlayer = (curPlayer + 1) % NUM_PLAYERS;
			content += "<META http-equiv=\"refresh\" content=\"0;url=/\"></HEAD><BODY>" +
					prevMsg + hangman.getHangmanHtml() + "Spieler " + curPlayer + " ist an der Reihe.";
			
		} else if(cookie == curPlayer)//TODO player is current player
			{
				content += "</HEAD><BODY>" +
						prevMsg + hangman.getHangmanHtml() + "Du bist an der Reihe, Spieler " + curPlayer + "!" +
						"<FORM action=\"/\" method=\"get\">";
				for (char i = 'a'; i <= 'z'; ++i) {
					content += "<INPUT type=\"submit\" name=\"letter\" value=\"" + i + "\">";
				}
				content += "</FORM><BR><FORM action=\"/\" method=\"get\">" +
						"<LABEL>Suchbegriff <INPUT name=\"solution\"></LABEL>" +
						"<BUTTON>Lösen</BUTTON></FORM>";
		} else {
			content += "<META http-equiv=\"refresh\" content=\"2;url=/\"></HEAD><BODY>" +
					prevMsg + hangman.getHangmanHtml() + "Spieler " + curPlayer + " ist an der Reihe.";
		}
		content += "</BODY></HTML>";
		
		//TODO send response to player
		bw.write(content+ EOL);
		bw.flush();
		
		if (hangman.win() || hangman.dead()) {
			gameStarted = false;
			gameEnded = true;
			curPlayer = 0;
		}
	}
	
	/*
	 * Handles HTTP conversation when game ended 
	 */
	private static void processEndRequest(BufferedReader br, BufferedWriter bw) throws Exception {
		//TODO Process request and header lines.
		String request = br.readLine();
		String header = "";
		String trash = "";
		
			
		while(header != "<CR><LF>") {
			request += header + "\n";
			header = br.readLine();
		}
		while(trash != "<CR><LF>") {
			trash = br.readLine();
		}
		
		String guess = "";
		
		// get Cookie
		String[] parts = header.split("\n");
		int cookie = 0;
		for(int i = 0; i<parts.length; i++) {
			if(parts[i].contains("Cookie")) {
				cookie = Integer.parseInt(parts[i].split("=")[1].replace(" ", ""));
			}
		}
		
		

		String content = "<HTML><HEAD><TITLE>Hangman</TITLE></HEAD><BODY>" +
				prevMsg + hangman.getHangmanHtml();

		// TODO Add success/fail line with solution word
		if(hangman.win()) {
			content += "Success: ";
		}
		else {
			content += "Fail: ";
		}
		
		content += hangman.getWord();
		
		content += "</BODY></HTML>";
		
		++curPlayer;
		//TODO send response to player
		bw.write(content+ EOL);
		bw.flush();
	}
	
	/*
	 * Returns request line of HTTP request if it is a valid game related request, else null
	 */
	private static String processHeaderLines(BufferedReader br, BufferedWriter bw) throws Exception {
		/*
		 * TODO
		 * Get the request line of the HTTP request message.
		 * Return null if its length is zero or if end of stream is reached.
		 * Print out the request line to the console.
		 * If the request is for "/favicon.ico", send a 404 response and return null.
		 */
		
		String request = br.readLine();
		if(request == null || request.length()==0) {
			return null;
		}
		System.out.println(request);
		
		if(request.contains("favicon.ico")) {
			bw.write("HTTP/1.1 404 Not Found" + EOL);
			bw.flush();
		}
		
		
		
		
		
		
		
		
		
		sessionCookie = -1;
		playerCookie = -1;
		//TODO Step through all remaining header lines and extract cookies if present (yamyam). Optionally print the header lines to the console.

		String header = "";
		String trash = "";
		
		while(header != "<CR><LF>") {
			request += header + "\n";
			header = br.readLine();
		}
		while(trash != "<CR><LF>") {
			trash = br.readLine();
		}
		
		String guess = "";
		
		// get Cookie
		String[] parts = header.split("\n");
		int cookie = 0;
		for(int i = 0; i<parts.length; i++) {
			if(parts[i].contains("Cookie")) {
				cookie = Integer.parseInt(parts[i].split("=")[1].replace(" ", ""));
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return requestLine;
	}
	
	/*
	 * Sends a 404-HTTP-response
	 */
	private static void sendNotFoundResponse(BufferedWriter bw) throws Exception {
		//TODO Construct and send a valid HTTP/1.0 404-response.

	
	
	
	
	
	
	}
	
	/*
	 * Sends a HTTP-Response with cookieLines if set and content
	 */
	private static void sendOkResponse(BufferedWriter bw, String cookieLines, String content) throws Exception {
		//TODO Construct and send a valid HTTP/1.0 200-response with the given cookies (if not null) and the given content.

	
	
	
	
	
	
	}
}
