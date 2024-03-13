package com.akash;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {
		
		String src="C:\\Users\\Akash\\Documents\\Java\\CoreJava\\FileCompressor\\src\\input.txt";
		String temp="C:\\Users\\Akash\\Documents\\Java\\CoreJava\\FileCompressor\\src\\temp.txt";
		String dest="C:\\Users\\Akash\\Documents\\Java\\CoreJava\\FileCompressor\\src\\output.txt";
		try {
			FileCompressor.encode(src, temp);
			FileCompressor.decode(temp, dest);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
}
