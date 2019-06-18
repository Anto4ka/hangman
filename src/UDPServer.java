import java.io.*;
import java.net.*;

class UDPServer { 
	public static void main(String args[]) throws Exception 
	{ 
		DatagramSocket serverSocket= new DatagramSocket(9876);
		byte[] receiveData= new byte[1024]; 
		byte[] sendData= new byte[1024]; 
		while(true) 
		{ 
			DatagramPacket receivePacket= new DatagramPacket(receiveData, receiveData.length); 
			serverSocket.receive(receivePacket); 
			
			int rcvLen = receivePacket.getLength();
			String sentence = new String(receivePacket.getData(), 0, rcvLen,"UTF-8"); 
			InetAddress ipAddress= receivePacket.getAddress(); 
			int port = receivePacket.getPort();
			String capitalizedSentence= sentence.toUpperCase(); 
			sendData = capitalizedSentence.getBytes("UTF-8"); 
			DatagramPacket sendPacket= new DatagramPacket(sendData,sendData.length, ipAddress, port); 
			serverSocket.send(sendPacket); 
		}
	}
}