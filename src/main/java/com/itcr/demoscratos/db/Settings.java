package com.itcr.demoscratos.db;

enum Settings {
	DRIVER("com.mysql.jdbc.Driver"),
	URL("jdbc:mysql://localhost:3306/Demoscratos"),
	USERNAME("root"),
	PASSWORD("root");
		
	private final String value;
	
	Settings(String value) {
		this.value = value; }

	String getValue() {
		return value; } }