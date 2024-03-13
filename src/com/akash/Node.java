package com.akash;

public class Node {
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
