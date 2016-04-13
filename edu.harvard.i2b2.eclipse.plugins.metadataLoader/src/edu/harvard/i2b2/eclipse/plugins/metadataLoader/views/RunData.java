/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.metadataLoader.views;


public class RunData {

	private String catalogLocation = null;
	private String metadataDirectory = null;
	private String metadataFile = null;
	private String schemesFile = null;
	private String tableAccessFile = null;
	private String metadataTable = null;

	private static RunData thisInstance;
	static {
		thisInstance = new RunData();
	}
		
	public static RunData getInstance() {
		return thisInstance;
	}


	public String getCatalogLocation() {
		return catalogLocation;
	}

	public void setCatalogLocation(String catalogLocation) {
		this.catalogLocation = catalogLocation;
	}


	public String getMetadataDirectory() {
		return metadataDirectory;
	}


	public void setMetadataDirectory(String metadataDirectory) {
		this.metadataDirectory = metadataDirectory;
	}


	public String getMetadataFile() {
		return metadataFile;
	}


	public void setMetadataFile(String metadataFile) {
		this.metadataFile = metadataFile;
	}


	public String getSchemesFile() {
		return schemesFile;
	}


	public void setSchemesFile(String schemesFile) {
		this.schemesFile = schemesFile;
	}


	public String getTableAccessFile() {
		return tableAccessFile;
	}


	public void setTableAccessFile(String tableAccessFile) {
		this.tableAccessFile = tableAccessFile;
	}
	

	public String getMetadataTable() {
		return metadataTable;
	}


	public void setMetadataTable(String metadataTable) {
		this.metadataTable = metadataTable;
	}	
}
