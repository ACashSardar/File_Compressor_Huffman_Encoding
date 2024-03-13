package com.akash;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class FileCompressor {
	
	static class Node{
		byte data;
		int freq;
		Node left;
		Node right;
		Node(byte data, int freq){
			this.data=data;
			this.freq=freq;
		}
		@Override
		public String toString() {
			return "Node [data=" + data + ", freq=" + freq + ", left=" + left + ", right=" + right + "]";
		}
	}
	
	public static void dfs(Node curr, StringBuilder path, Map<Byte, String> encoderMap, Map<String, Byte> decoderMap) {
        if(curr.left==null && curr.right==null){
        	encoderMap.put(curr.data, path.toString());
        	decoderMap.put(path.toString(), curr.data);
            return;
        }
        path.append("0");
        dfs(curr.left, path, encoderMap, decoderMap);
        path.deleteCharAt(path.length() - 1);
        path.append("1");
        dfs(curr.right, path, encoderMap, decoderMap);
        path.deleteCharAt(path.length() - 1);
	}
	
	public static void encode(String src, String dest) throws IOException, ClassNotFoundException {
		
		InputStream inputStream=new FileInputStream(src);
		int data=0;
		List<Byte> bytes=new ArrayList<>();
		while ((data=inputStream.read())!=-1) {
			bytes.add((byte)data);
		}
		
		Map<Byte, Integer> map=new HashMap<>();
		Queue<Node> q=new PriorityQueue<>((a, b)->a.freq<b.freq ? -1 : 1);
		Map<Byte, String> encoderMap=new HashMap<>();
		Map<String, Byte> decoderMap=new HashMap<>();
		
		for(byte b: bytes) {
			if(map.containsKey(b)) {
				map.put(b, map.get(b)+1);
			}else {
				map.put(b, 1);
			}
		}
		
		for(var itr: map.entrySet()) {
			q.add(new Node(itr.getKey(), itr.getValue()));
		}
		
		while(q.size()>1) {
			Node a=q.poll();
			Node b=q.poll();
			Node c=new Node((byte)0, a.freq+b.freq);
			c.left=a;
			c.right=b;
			q.add(c);
		}
		dfs(q.poll(), new StringBuilder(), encoderMap, decoderMap);

		StringBuilder encodedMessage=new StringBuilder();
		for(byte b: bytes) {
			encodedMessage.append(encoderMap.get(b));
		}
		int[] extraZeros= {0};
		while(encodedMessage.length()%7!=0) {
			encodedMessage.append("0");
			extraZeros[0]++;
		}
		int len=encodedMessage.length()/7;
		byte[] compressedData=new byte[len];
		int j=0;
		for(int i=0; i<encodedMessage.length(); i+=7) {
			String bin=encodedMessage.substring(i, i+7).toString();
			int val=Integer.parseInt(bin, 2);
			compressedData[j++]=(byte)val;
		}
		
		OutputStream outputStream=new FileOutputStream(dest);
		ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(compressedData);
		objectOutputStream.writeObject(extraZeros);
		objectOutputStream.writeObject(decoderMap);
		objectOutputStream.close();
		outputStream.close();
		inputStream.close();
	}
	
	public static void decode(String src, String dest) throws ClassNotFoundException, IOException {
		InputStream inputStream=new FileInputStream(src);
		ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
		
		byte[] bytes=(byte[])objectInputStream.readObject();
		int[] extraZeros=(int[])objectInputStream.readObject();
		@SuppressWarnings("unchecked")
		Map<String, Byte> decoderMap=(Map<String, Byte>)objectInputStream.readObject();

		
		StringBuilder sb=new StringBuilder();
		for(byte b: bytes) {
			String s = String.format("%7s", Integer.toBinaryString(b & 0x7F)).replace(' ', '0');
			sb.append(s);
		}
		
		while(extraZeros[0]>0) {
			sb.deleteCharAt(sb.length()-1);
			extraZeros[0]--;
		}

		int prev=0;
		StringBuilder ans=new StringBuilder();
		for(int i=0; i<sb.length(); i++) {
			String key=sb.substring(prev, i+1).toString();
			if(decoderMap.containsKey(key)) {
				byte b=decoderMap.get(key);
				ans.append((char)b);
				prev=i+1;
			}
		}
		OutputStream outputStream=new FileOutputStream(dest);
		outputStream.write(ans.toString().getBytes());
		
		outputStream.close();
		inputStream.close();
		objectInputStream.close();
	}
}
