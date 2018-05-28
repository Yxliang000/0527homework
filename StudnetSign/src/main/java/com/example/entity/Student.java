package com.example.entity;

public class Student {
	private String name;
	private boolean signState;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSignState() {
		return signState;
	}
	public void setState(boolean signState) {
		this.signState = signState;
	}
	public Student(String name, boolean signState) {
		this.name = name;
		this.signState = signState;
	}
	public Student() {
	}
	@Override
	public String toString() {
		return "signState=" + signState ;
	}
	
	
}
