/*
 * Copyright (c) 2006-2014 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Raj Kuttan
 * 		Lori Phillips
 */

package edu.harvard.i2b2.eclipse.plugins.metadataLoader.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.BodyType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.MessageHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestHeaderType;
import edu.harvard.i2b2.ontclient.datavo.i2b2message.RequestMessageType;
import edu.harvard.i2b2.ontclient.datavo.vdo.MetadataLoadType;

public class LoadMetadataRequestMessage extends OntologyRequestData {
	
	public static final String THIS_CLASS_NAME = LoadMetadataRequestMessage.class.getName();
    private Log log = LogFactory.getLog(THIS_CLASS_NAME);	

    public LoadMetadataRequestMessage() {}
	
	/**
	 * Function to build get metadataLoad type for a given request
	 * 
	 * @return MetadataLoadType object
	 */
	public MetadataLoadType MetadataLoadType() { 
		MetadataLoadType returnType = new MetadataLoadType();		
		return returnType;
	}
		
	/**
	 * Function to build metadataLoad body type
	 * 
	 * @param 
	 * @return BodyType object
	 */
	
	public BodyType getBodyType() {
		MetadataLoadType returnType = MetadataLoadType();
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createLoadMetadata(returnType));
		return bodyType;
	}
	
	/**
	 * Function to build returnType body type
	 * 
	 * @param MetadataLoadType returnType
	 * @return BodyType object
	 */
	
	public BodyType getBodyType(MetadataLoadType returnType) {
		edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory of = new edu.harvard.i2b2.ontclient.datavo.vdo.ObjectFactory();
		
		BodyType bodyType = new BodyType();
		bodyType.getAny().add(of.createLoadMetadata(returnType));
		return bodyType;
	}
	
	/**
	 * Function to build Ont Request message type and return it as an XML string
	 * 
	 * @param MetadataLoadType returnData 
	 * @return A String data type containing the Ont RequestMessage in XML format
	 */
	public String doBuildXML(MetadataLoadType returnData){ 
		String requestString = null;
			try {
				MessageHeaderType messageHeader = getMessageHeader(); 
				RequestHeaderType reqHeader  = getRequestHeader();
				BodyType bodyType = getBodyType(returnData) ;
				RequestMessageType reqMessageType = getRequestMessageType(messageHeader,
						reqHeader, bodyType);
				requestString = getXMLString(reqMessageType);
			} catch (JAXBUtilException e) {
				log.error(e.getMessage());
			} 
		return requestString;
	}

}
