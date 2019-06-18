import	java.io.*; 
import	java.net.*; 


public class TCPclient {
	
	public static void main(String argv[]) throws Exception 
	{ 
		String sentence; 
		String modifiedSentence; 
		
		Socket clientSocket = new Socket("hurensohn", 6789);
		
		BufferedWriter outToServer= new BufferedWriter(
				new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
		
		BufferedReader inFromServer = 
				new BufferedReader(
						new InputStreamReader(
								clientSocket.getInputStream(), "UTF-8"));
		
		sentence = "abc";
		
		for(int i = 0; i<=1000; i++) {
			outToServer.write(Integer.toString(i) + "\n");
			
			System.out.println(i);
			
		}
		outToServer.flush();
		
		
		modifiedSentence = inFromServer.readLine(); 
		System.out.println("FROM SERVER: "+ modifiedSentence); 
		
		clientSocket.close();
	}
}
