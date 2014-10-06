package com.synload.socketframework.clienttest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.socketframework.encoding.Transmission;
import com.socketframework.encoding.TransmissionMessage;


public class Main {
	public static void main(String[] args){
		try {
			Socket s = new Socket("synload.com",2006);
			s.setTcpNoDelay(true);
			
				// What do to with the transmission!
				String fileName = sendFile( s, "./1380850576684.jpg" );
				System.out.println("Sent: "+fileName);
				fileName = sendFile( s, "./mRXWlafb-.mp4" );
				System.out.println("Sent: "+fileName);
				// Done with communication
				
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String sendFile(Socket s, String fileName) throws IOException{
		
		ByteArrayOutputStream ltob = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(ltob);
		dos.writeLong((new File(fileName)).length());
		byte[] commandEncode = Transmission.encodeMessage( new byte[]{0x01,0x01,0x01}, ltob.toByteArray());
		ltob.close();
		dos.close();
		
		s.getOutputStream().write(commandEncode);
		s.getOutputStream().flush();
		
		InputStream is = new FileInputStream(new File(fileName));
		byte[] buffer = new byte[8*1024];
		int readData = 0;
		while((readData = is.read(buffer))!=-1){
			s.getOutputStream().write(Arrays.copyOf(buffer, readData));
			s.getOutputStream().flush();
		}
		is.close();
		String i = "";
		while((readData = s.getInputStream().read(buffer))!=-1){
			byte[] data = Arrays.copyOf(buffer, readData);
			TransmissionMessage m = Transmission.decodeStringMessage(data);
			if(m.getCommand()[0]==0x01 && m.getCommand()[1]==0x01 && m.getCommand()[2]==0x01){
				i = (String) m.getValue();
				break;
			}
		}
		
		commandEncode = Transmission.encodeMessage( new byte[]{0x01,0x01,0x02}, i.getBytes());
		
		s.getOutputStream().write(commandEncode);
		s.getOutputStream().flush();
		
		long filesize = 0;
		while((readData = s.getInputStream().read(buffer))!=-1){
			byte[] data = Arrays.copyOf(buffer, readData);
			TransmissionMessage m = Transmission.decodeLongMessage(data);
			if(m.getCommand()[0]==0x01 && m.getCommand()[1]==0x01 && m.getCommand()[2]==0x02){
				filesize = (long) m.getValue();
				break;
			}
		}
		System.out.println("filesize: "+filesize);
		
		return i;
	}
}
