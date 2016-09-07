/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 *      Raj Kuttan
 */

package edu.harvard.i2b2.eclipse.plugins.metadataLoader.ws;

import java.io.StringReader;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.util.MessageUtil;

import edu.harvard.i2b2.ontclient.datavo.vdo.GetCategoriesType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetChildrenType;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;
import edu.harvard.i2b2.ontclient.datavo.vdo.MetadataLoadType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyLoadType;
import edu.harvard.i2b2.ontclient.datavo.vdo.VocabRequestType;
import edu.harvard.i2b2.common.exception.I2B2Exception;


/* 
 * Copied ontology functions to mapper cell; reusing this code by pointing it to map cell.
 */

public class OntServiceDriver {

	public static final String THIS_CLASS_NAME = OntServiceDriver.class.getName();
    private static Log log = LogFactory.getLog(THIS_CLASS_NAME);
//    private static String serviceURL = UserInfoBean.getInstance().getCellDataUrl("map");
    private static String ontServiceUrl = UserInfoBean.getInstance().getCellDataUrl("ont");
//    private static String serviceMethod = UserInfoBean.getInstance().getCellDataMethod("map").toUpperCase();
	private static EndpointReference soapEPR = new EndpointReference(ontServiceUrl);
	
	private static EndpointReference childrenEPR = new EndpointReference(
			ontServiceUrl + "getChildren");

	private static EndpointReference categoriesEPR = new EndpointReference(
		ontServiceUrl + "getCategories");
	
	private static EndpointReference schemesEPR = new EndpointReference(
			ontServiceUrl + "getSchemes");

	private static EndpointReference nameInfoEPR = new EndpointReference(
			ontServiceUrl + "getNameInfo");
	
	private static EndpointReference codeInfoEPR = new EndpointReference(
			ontServiceUrl + "getCodeInfo");
	
	private static EndpointReference loadMetadataEPR = new EndpointReference(
			ontServiceUrl + "loadMetadata");
	
	private static EndpointReference checkTableExistenceEPR = new EndpointReference(
			ontServiceUrl + "checkForTableExistence");
	
	
    public static OMElement getVersion() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "tns");

        OMElement method = fac.createOMElement("getVersion", omNs);

        return method;
    }
	
	/**
	 * Function to send getChildren requestVdo to ONT web service
	 * 
	 * @param GetChildrenType  parentNode we wish to get data for
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getChildren(GetChildrenType parentNode, String type) throws Exception{
		String response = null;
		
			 try {
				 parentNode.setMax(2000);
				 GetChildrenRequestMessage reqMsg = new GetChildrenRequestMessage();

				 String getChildrenRequestString = reqMsg.doBuildXML(parentNode, type);
	//			 log.debug(getChildrenRequestString);
				 				 
			
				
					 response = sendREST(childrenEPR, getChildrenRequestString, type);
				
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error(e.getMessage());
				throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
    
	/**
	 * Function to send getCategories requestVdo to ONT web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getCategories(GetCategoriesType returnData, String type) throws Exception {
		String response = null;
			 try {
				 GetCategoriesRequestMessage reqMsg = new GetCategoriesRequestMessage();
				 String getCategoriesRequestString = reqMsg.doBuildXML(returnData);
	//			log.debug(getCategoriesRequestString); 
			
				
					response = sendREST(categoriesEPR, getCategoriesRequestString, type);
				
	//			log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error(e.getMessage());
				throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	 /**
	 * Function to send getSchemes requestVdo to ONT web service
	 * 
	 * @param GetReturnType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getSchemes(GetReturnType returnData, String type) throws Exception{
		String response = null;
			 try {
				 GetSchemesRequestMessage reqMsg = new GetSchemesRequestMessage();
				 String getSchemesRequestString = reqMsg.doBuildXML(returnData);

	//			log.debug(getSchemesRequestString);
				
	
					response = sendREST(schemesEPR, getSchemesRequestString, type);
			
//				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (I2B2Exception e) {
				log.error(e.getMessage());
				throw new I2B2Exception(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}

	/**
	 * Function to send getNameInfo requestVdo to ONT web service
	 * 
	 * @param VocabRequestType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getNameInfo(VocabRequestType vocabData, String type) throws Exception{
		String response = null;
			 try {
				 GetNameInfoRequestMessage reqMsg = new GetNameInfoRequestMessage();
				 String getNameInfoRequestString = reqMsg.doBuildXML(vocabData);

						response = sendREST(nameInfoEPR, getNameInfoRequestString, type);
				

//				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	/**
	 * Function to send getCodeInfo requestVdo to ONT web service
	 * 
	 * @param VocabRequestType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String getCodeInfo(VocabRequestType vocabData, String type) throws Exception{
		String response = null;
			 try {
				 GetCodeInfoRequestMessage reqMsg = new GetCodeInfoRequestMessage();
				 String getCodeInfoRequestString = reqMsg.doBuildXML(vocabData);

						response = sendREST(codeInfoEPR, getCodeInfoRequestString, type);
				

//				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	/**
	 * Function to send loadMetadata requestVdo to ONT web service
	 * 
	 * @param MetadataLoadType  return parameters 
	 * @return A String containing the ONT web service response 
	 */
	
	public static String loadMetadata(MetadataLoadType vocabData, String type) throws Exception{
		String response = null;
			 try {
				 LoadMetadataRequestMessage reqMsg = new LoadMetadataRequestMessage();
				 String loadMetadataRequestString = reqMsg.doBuildXML(vocabData);

						response = sendREST(loadMetadataEPR, loadMetadataRequestString, type);
				

//				log.debug("Ont response = " + response);
			} catch (AxisFault e) {
				log.error(e.getMessage());
				throw new AxisFault(e);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new Exception(e);
			}
		return response;
	}
	
	public static String checkForTableExistence(MetadataLoadType vocabData, String type) throws Exception{
		String response = null;
		 try {
			 LoadMetadataRequestMessage reqMsg = new LoadMetadataRequestMessage();
			 String loadMetadataRequestString = reqMsg.doBuildXML(vocabData);

			response = sendREST(checkTableExistenceEPR, loadMetadataRequestString, type);
			

//			log.debug("Ont response = " + response);
		} catch (AxisFault e) {
			log.error(e.getMessage());
			throw new AxisFault(e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return response;
	}
	
	/**
	 * Function to convert Ont requestVdo to OMElement
	 * 
	 * @param requestVdo   String requestVdo to send to Ont web service
	 * @return An OMElement containing the Ont web service requestVdo
	 */
	public static OMElement getOntPayLoad(String requestVdo) throws Exception {
		OMElement lineItem  = null;
		try {
			StringReader strReader = new StringReader(requestVdo);
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader reader = xif.createXMLStreamReader(strReader);

			StAXOMBuilder builder = new StAXOMBuilder(reader);
			lineItem = builder.getDocumentElement();
		} catch (FactoryConfigurationError e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
		return lineItem;
	}
	

	
	public static String sendREST(EndpointReference restEPR, String requestString, String type) throws Exception{	

//		requestString.replaceAll("\\p{Cntrl}", "");  did not fix illegal control char error
		OMElement getOnt = getOntPayLoad(requestString);
		MessageUtil.getInstance().setNavRequest("URL: " + restEPR + "\n" + getOnt.toString());
		
		
		Options options = new Options();
		log.debug(restEPR.toString());
		options.setTo(restEPR);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

		options.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		options.setProperty(HTTPConstants.SO_TIMEOUT,new Integer(125000));
		options.setProperty(HTTPConstants.CONNECTION_TIMEOUT,new Integer(125000));

		ServiceClient sender = OntServiceClient.getServiceClient();
		sender.setOptions(options);

		OMElement result;
		try {
			result = sender.sendReceive(getOnt);
		} catch (java.lang.OutOfMemoryError e) {
			System.gc();
			throw new I2B2Exception("Out of memory");
//			return null;
		}
		String response = result.toString();
		MessageUtil.getInstance().setNavResponse("URL: " + restEPR + "\n" + response);
		
		
		return response;

	}


	

	
}
