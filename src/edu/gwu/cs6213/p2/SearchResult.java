package edu.gwu.cs6213.p2;

public class SearchResult {
	private String sourceFile;
	private String name;
	private String phoneNumber;

	public SearchResult(String sourceFile, String record){
		this.sourceFile = sourceFile;
		String []data = record.split(",");
		this.name= data[0];
		this.phoneNumber = data[1];
	}
	public String getName() {
		return name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	@Override
	public String toString() {
		return "Name: "+name +", PhoneNumber:"+phoneNumber;
	}
}
