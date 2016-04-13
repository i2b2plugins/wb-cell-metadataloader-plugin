/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
 package edu.harvard.i2b2.eclipse.plugins.metadataLoader.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.harvard.i2b2.ontclient.datavo.vdo.ConceptType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyDataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.XmlValueType;


public class MetadataLoaderType extends OntologyDataType {


	public OntologyDataType fromTableAccess(Vector<String> fields) {
//		"C_TABLE_CD"|"C_TABLE_NAME"|"C_PROTECTED_ACCESS"|"C_HLEVEL"|"C_FULLNAME"|"C_NAME"|"C_SYNONYM_CD"|"C_VISUALATTRIBUTES"|"C_TOTALNUM"|"C_BASECODE"|"C_METADATAXML"|"C_FACTTABLECOLUMN"|"C_DIMTABLENAME"|"C_COLUMNNAME"|"C_COLUMNDATATYPE"|"C_OPERATOR"|"C_DIMCODE"|"C_COMMENT"|"C_TOOLTIP"|"C_ENTRY_DATE"|"C_CHANGE_DATE"|"C_STATUS_CD"|"VALUETYPE_CD"
//
		OntologyDataType ontData = new OntologyDataType();
		ontData.setTableCd(handleNull(fields.get(0)));
		ontData.setTableName(handleNull(fields.get(1)));
		ontData.setProtectedAccess(handleNull(fields.get(2)));
		ontData.setLevel(Integer.parseInt(handleNull(fields.get(3))));
		ontData.setFullname(handleNull(fields.get(4)));
		ontData.setName(handleNull(fields.get(5)));
		ontData.setSynonymCd(handleNull(fields.get(6)));
		ontData.setVisualattributes(handleNull(fields.get(7)));
		if(handleNull(fields.get(8)) == null)
			ontData.setTotalnum(null);
		else
			ontData.setTotalnum(Integer.parseInt(handleNull(fields.get(8))));
		ontData.setBasecode(handleNull(fields.get(9)));
	//	ontData.setMetadataxml(fields.get(10));
		ontData.setMetadataxml(null);
		
		ontData.setFacttablecolumn(handleNull(fields.get(11)));
		ontData.setDimtablename(handleNull(fields.get(12)));
		ontData.setColumnname(handleNull(fields.get(13)));
		ontData.setColumndatatype(handleNull(fields.get(14)));
		ontData.setOperator(handleNull(fields.get(15)));
		ontData.setDimcode(handleNull(fields.get(16)));
		ontData.setComment(handleNull(fields.get(17)));
		ontData.setTooltip(handleNull(fields.get(18)));

		
		// EntryDate 19, changeDate 20
	
      	try {
      		if(handleNull(fields.get(19)) == null)
      			ontData.setEntryDate(null);
      		else{
      			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss"); 
      			GregorianCalendar gc = new GregorianCalendar();
      				gc.setTimeInMillis(sdf.parse(handleNull(fields.get(19))).getTime());
      			ontData.setEntryDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      	try {
      		if(handleNull(fields.get(20)) == null)
      			ontData.setChangeDate(null);
      		else{
      			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss"); 
      			GregorianCalendar gc = new GregorianCalendar();
      				gc.setTimeInMillis(sdf.parse(handleNull(fields.get(20))).getTime());
      			ontData.setChangeDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		ontData.setStatusCd(handleNull(fields.get(21)));
		ontData.setValuetypeCd(handleNull(fields.get(22)));
	
		return ontData;
		
	}
	
	public OntologyDataType fromSchemes(Vector<String> fields) {
		//	"C_KEY"|"C_NAME"|"C_DESCRIPTION"
		OntologyDataType ontData = new OntologyDataType();
		ontData.setKey(handleNull(fields.get(0)));
		ontData.setName(handleNull(fields.get(1)));
		ontData.setDescription(handleNull(fields.get(2)));
	
		return ontData;
		
	}
	
	public OntologyDataType fromMetadata(Vector<String> fields) {
//		"C_HLEVEL"|"C_FULLNAME"|"C_NAME"|"C_SYNONYM_CD"|"C_VISUALATTRIBUTES"|"C_TOTALNUM"|"C_BASECODE"|"C_METADATAXML"|"C_FACTTABLECOLUMN"|"C_TABLENAME"|"C_COLUMNNAME"|"C_COLUMNDATATYPE"|"C_OPERATOR"|"C_DIMCODE"|"C_COMMENT"|"C_TOOLTIP"|"M_APPLIED_PATH"|"UPDATE_DATE"|"DOWNLOAD_DATE"|"IMPORT_DATE"|"SOURCESYSTEM_CD"|"VALUETYPE_CD"|"M_EXCLUSION_CD"|"C_PATH"|"C_SYMBOL
		OntologyDataType ontData = new OntologyDataType();
		
		ontData.setLevel(Integer.parseInt(handleNull(fields.get(0))));
		ontData.setFullname(handleString(handleNull(fields.get(1))));
		ontData.setName(handleString(handleNull(fields.get(2))));
		ontData.setSynonymCd(handleNull(fields.get(3)));
		ontData.setVisualattributes(handleNull(fields.get(4)));
		if(handleNull(fields.get(5)) == null)
			ontData.setTotalnum(null);
		else
			ontData.setTotalnum(Integer.parseInt(handleNull(fields.get(5))));
		ontData.setBasecode(handleNull(fields.get(6)));
		ontData.setMetadataxml(setXmlValue(ontData, handleNull(fields.get(7))));
		
		ontData.setFacttablecolumn(handleNull(fields.get(8)));
		ontData.setDimtablename(handleNull(fields.get(9)));
		ontData.setColumnname(handleNull(fields.get(10)));
		ontData.setColumndatatype(handleNull(fields.get(11)));
		ontData.setOperator(handleNull(fields.get(12)));
		ontData.setDimcode(handleNull(fields.get(13)));
		ontData.setComment(handleString(handleNull(fields.get(14))));
		ontData.setTooltip(handleNull(fields.get(15)));
		ontData.setAppliedPath(handleNull(fields.get(16)));
		
		// updateDate 17, downloadDate 18, importDate 19
		   
      	try {
      		if(handleNull(fields.get(17)) == null)
      			ontData.setUpdateDate(null);
      		else{
      			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a"); 
      			GregorianCalendar gc = new GregorianCalendar();
      				gc.setTimeInMillis(sdf.parse(handleNull(fields.get(17))).getTime());
      			ontData.setUpdateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
      	try {
      		if(handleNull(fields.get(18)) == null)
      			ontData.setDownloadDate(null);
      		else{
      			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a"); 
      			GregorianCalendar gc = new GregorianCalendar();
      				gc.setTimeInMillis(sdf.parse(handleNull(fields.get(18))).getTime());
      			ontData.setDownloadDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			ontData.setDownloadDate(null);
		}
		catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			ontData.setDownloadDate(null);
		}
		
      	try {
      		if(handleNull(fields.get(19)) == null)
      			ontData.setImportDate(null);
      		else{
      			GregorianCalendar gc = new GregorianCalendar();

      			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss a"); 
      			gc.setTimeInMillis(sdf.parse(handleNull(fields.get(19))).getTime());

      			ontData.setImportDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
      		}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			ontData.setImportDate(null);
		}
		catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			ontData.setImportDate(null);
		}
		
		ontData.setSourcesystemCd(handleNull(fields.get(20)));
		ontData.setValuetypeCd(handleNull(fields.get(21)));
		ontData.setExclusionCd(handleNull(fields.get(22)));
		ontData.setPath(handleNull(fields.get(23)));
		ontData.setSymbol(handleNull(fields.get(24)));
		
		if(fields.size() == 26)
			ontData.setPcoriBasecode(handleNull(fields.get(25)));
		
		return ontData;
	
	}
	
	
	private String handleNull(String term){
		if (term == null)
			return null;
		else{
			if (term.length() == 0)
				return null;
			else if(term.equals("(null)"))
				return null;
			
			else if(term.equals("null"))
				return null;
			
			else if(term.equals("NULL"))
				return null;
			else
				return term;
		}
	}
	
	
	private XmlValueType setXmlValue(OntologyDataType ontData, String c_xml){
		XmlValueType xml = null;
		if(c_xml == null)
			return xml;
		c_xml = c_xml.replaceAll("\"\"", "\"");
		SAXBuilder parser = new SAXBuilder();
		java.io.StringReader xmlStringReader = new java.io.StringReader(c_xml);
		Element rootElement = null;
		try {
			org.jdom.Document metadataDoc = parser.build(xmlStringReader);
			//clear out the jaxb namespace...
			metadataDoc.getRootElement().setNamespace(null);
			
			org.jdom.output.DOMOutputter out = new DOMOutputter(); 
			Document doc = out.output(metadataDoc);
			rootElement = doc.getDocumentElement();
		
		} catch (JDOMException e) {
			System.out.println(e.getMessage());
			System.out.println(ontData.getFullname());
		//	log.error(e.getMessage());
		//	child.setMetadataxml(null);
		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		//	log.error(e1.getMessage());
		//	child.setMetadataxml(null);
		}
		if (rootElement != null) {
			
			xml = new XmlValueType();
			xml.getAny().add(rootElement);
			return xml;
		}
		return xml;
	}
	
	private String handleString(String string) {
		if(string == null)
			return string;
		string = string.replaceAll("\"\"", "\"");
		return string;
	}

}
