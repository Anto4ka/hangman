import java.io.*;
import java.net.*;

public class TCPServer {
	public static void main(String argv[]) throws Exception 
	{ 
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		
		while(true) 
		{ 
			Socket connectSocket = welcomeSocket.accept();
			BufferedReader inFromClient= new BufferedReader(
					new InputStreamReader(connectSocket.getInputStream(),"UTF-8"));
			
			BufferedWriter outToClient= new BufferedWriter(
					new OutputStreamWriter(connectSocket.getOutputStream(), "UTF-8")); 
			
			String[] intArray = new String[1000];
			
			
			for(int k = 0; k < 1000; k++) {
				clientSentence = inFromClient.readLine(); 
				intArray[k] = clientSentence;
			}
			
			
			String wrongNumbers = "";
			
			for(int k = 0; k < 1000; k++) {
				String kString = Integer.toString(k);
				if(!intArray[k].equals(kString)){
					wrongNumbers += kString + " ";
				}
			}
			
			if(wrongNumbers.length() == 0) {
				outToClient.write("Everything Correct!\n");
				outToClient.flush();
				connectSocket.close();
			}
			else {
				outToClient.write("Wrong: " + wrongNumbers + "\n");
				outToClient.flush();
				connectSocket.close();
			}
		}
		
		
		
	}
}
