import java.io.*;
import java.net.*;

class UDPClient { 
	public static void main(String args[]) throws Exception { 
		BufferedReader inFromUser = new BufferedReader(
				new InputStreamReader(System.in)); 
		DatagramSocket clientSocket = new DatagramSocket(); 
		InetAddress ipAddress= InetAddress.getByName("localhost"); 
		byte[] sendData= new byte[1024];
		byte[] receiveData= new byte[1024];
		String sentence = inFromUser.readLine(); 
		sendData= sentence.getBytes("UTF-8"); 
		
		DatagramPacket sendPacket= new DatagramPacket(sendData,sendData.length, ipAddress, 9876); 
		clientSocket.send(sendPacket); 
		DatagramPacket receivePacket= new DatagramPacket(receiveData, receiveData.length); 
		clientSocket.receive(receivePacket);
		int rcvLen= receivePacket.getLength();
		String modifiedSentence= new String(receivePacket.getData(), 0, rcvLen, "UTF-8");
		System.out.println("FROM SERVER: "+ modifiedSentence); clientSocket.close(); 
	}
}