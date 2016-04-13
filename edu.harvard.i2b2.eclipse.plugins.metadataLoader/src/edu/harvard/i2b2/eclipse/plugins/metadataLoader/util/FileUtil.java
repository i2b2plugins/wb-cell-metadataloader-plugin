/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.metadataLoader.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.RunData;




/**
 * StringUtil class to perform string parsing tasks
 * This is singleton class.
 * @author lcp5
 */
public class FileUtil {

    //to make this class singleton
    private static FileUtil thisInstance;
    
    static {
    	thisInstance = new FileUtil();
    }

    public static FileUtil getInstance() {
    	return thisInstance;
    }


    public static void unzip(String zipname) throws IOException {
  
    	FileInputStream fis = new FileInputStream(zipname);
    	ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
    	//??    GZIPInputStream gzis = new GZIPInputStream(new BufferedInputStream(fis));

		// get directory of the zip file
		if(zipname.contains("\\"))
			zipname = zipname.replace("\\","/");
	//	String zipDirectory = zipname.substring(0, zipname.lastIndexOf("/")+1) ;
		String zipDirectory = zipname.substring(0, zipname.lastIndexOf(".")) ;
		new File(zipDirectory).mkdir();
		
		RunData.getInstance().setMetadataDirectory(zipDirectory);
		
    	ZipEntry entry;
    	while ((entry = zis.getNextEntry()) != null) {
    		System.out.println("Unzipping: " + entry.getName());
    		if(entry.getName().contains("metadata")){
    			RunData.getInstance().setMetadataFile(zipDirectory + "/" + entry.getName());	
    		}
    		else if(entry.getName().contains("schemes")){
    			RunData.getInstance().setSchemesFile(zipDirectory + "/" + entry.getName());	
    		}
    		else if(entry.getName().contains("access")){
    			RunData.getInstance().setTableAccessFile(zipDirectory + "/" + entry.getName());	
    		}

    		int size;
    		byte[] buffer = new byte[2048];
    
    		FileOutputStream fos = new FileOutputStream(zipDirectory + "/" + entry.getName());
    		BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);

    		while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
    			bos.write(buffer, 0, size);
    		}
    		bos.flush();
    		bos.close();
    	}
    	zis.close();
    	fis.close();
    }

    //http://www.java2s.com/Code/Java/File-Input-Output/Zip-Tar-File.htm

	

// this might work if the zip file contains a folder..
	public static void unZipAll(File source, File destination) throws IOException 
	{
	    System.out.println("Unzipping - " + source.getName());
	    int BUFFER = 2048;

	    ZipFile zip = new ZipFile(source);
	    try{
	        destination.getParentFile().mkdirs();
	        Enumeration zipFileEntries = zip.entries();

	        // Process each entry
	        while (zipFileEntries.hasMoreElements())
	        {
	            // grab a zip file entry
	            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	            String currentEntry = entry.getName();
	            File destFile = new File(destination, currentEntry);
	            //destFile = new File(newPath, destFile.getName());
	            File destinationParent = destFile.getParentFile();

	            // create the parent directory structure if needed
	            destinationParent.mkdirs();

	            if (!entry.isDirectory())
	            {
	                BufferedInputStream is = null;
	                FileOutputStream fos = null;
	                BufferedOutputStream dest = null;
	                try{
	                    is = new BufferedInputStream(zip.getInputStream(entry));
	                    int currentByte;
	                    // establish buffer for writing file
	                    byte data[] = new byte[BUFFER];

	                    // write the current file to disk
	                    fos = new FileOutputStream(destFile);
	                    dest = new BufferedOutputStream(fos, BUFFER);

	                    // read and write until last byte is encountered
	                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	                        dest.write(data, 0, currentByte);
	                    }
	                } catch (Exception e){
	                    System.out.println("unable to extract entry:" + entry.getName());
	                    throw e;
	                } finally{
	                    if (dest != null){
	                        dest.close();
	                    }
	                    if (fos != null){
	                        fos.close();
	                    }
	                    if (is != null){
	                        is.close();
	                    }
	                }
	            }else{
	                //Create directory
	                destFile.mkdirs();
	            }

	            if (currentEntry.endsWith(".zip"))
	            {
	                // found a zip file, try to extract
	                unZipAll(destFile, destinationParent);
	                if(!destFile.delete()){
	                    System.out.println("Could not delete zip");
	                }
	            }
	        }
	    } catch(Exception e){
	        e.printStackTrace();
	        System.out.println("Failed to successfully unzip:" + source.getName());
	    } finally {
	        zip.close();
	    }
	    System.out.println("Done Unzipping:" + source.getName());
	}

	public static void download(String workingDir, String URL, String filename) throws IOException {
		workingDir = workingDir + filename;		


		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(URL);

		HttpResponse response = httpclient.execute(httpget);

		System.out.println(response.getStatusLine());
		HttpEntity entity = response.getEntity();
		if (entity != null) {

			InputStream instream = entity.getContent();

			try {
				BufferedInputStream bis = new BufferedInputStream(instream);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(workingDir)));
				int inByte;
				while ((inByte = bis.read()) != -1 ) {
					bos.write(inByte);
				}
				bis.close();
				bos.close();
				//		 unzip("ICD10.zip");
			} catch (IOException ex) {
				throw ex;
			} catch (RuntimeException ex) {
				httpget.abort();
				throw ex;
			} finally {
				instream.close();
			}
			httpclient.getConnectionManager().shutdown();

		}
	}


	
}