/*
* Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips 
 */
package edu.harvard.i2b2.eclipse.plugins.metadataLoader.model;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyLoadType;

public class MetadataLabelProvider  extends LabelProvider implements	ITableLabelProvider {
	
	public static final Class THIS_CLASS = MetadataLabelProvider.class;	

//	private static final Image CHECKED = ImageDescriptor.createFromFile(THIS_CLASS,
//				"icons/checkBox_checked_Black.png").createImage();

//	private static final Image UNCHECKED = ImageDescriptor.createFromFile(THIS_CLASS,
//				"icons/checkBox_unchecked.png").createImage();
	
//	private static final Image CONSERVATION = ImageDescriptor.createFromFile(THIS_CLASS,
//	"icons/conservation.png").createImage();

	private TableViewer viewer;
	private Display display;
	private FontRegistry fontRegistry;
	private ColorRegistry colorRegistry;
	
	public MetadataLabelProvider(TableViewer viewer, Display display) {
		this.viewer = viewer;
		this.display = display;
		fontRegistry = new FontRegistry();
		createFontRegistry();
		colorRegistry = new ColorRegistry();
		createColorRegistry();
	}
	
	private void createFontRegistry()
	{
		Font defaultFont = this.fontRegistry.defaultFont();
		this.fontRegistry.put("default", defaultFont.getFontData());

	}
	
	private void createColorRegistry(){
	
	//	Color colorGreen = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	//	Color colorCyan = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
	//	Color colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	//	Color colorYellow = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);

		Color defaultColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		
		this.colorRegistry.put("default", defaultColor.getRGB());
//		this.colorRegistry.put("green", colorGreen.getRGB());
//		this.colorRegistry.put("red", colorRed.getRGB());
//		this.colorRegistry.put("cyan", colorCyan.getRGB());
//		this.colorRegistry.put("yellow", colorYellow.getRGB());
			
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// In case you don't like image just return null here

		return null;
	}


	@Override
	public String getColumnText(Object element, int columnIndex) {
		OntologyLoadType ont = (OntologyLoadType) element;
		
		TableItem item = (TableItem) (viewer.testFindItem(element));
		item.setFont(fontRegistry.get("default"));
		
	//	String colorKey = SchemesHash.getInstance().getColorKey(term.getSourceCodingSystem());

	//	if(colorKey == null)
		String	colorKey = "default";
		item.setForeground(colorRegistry.get(colorKey));
		
		String tooltip = ont.getDescription(); 	
		if ((tooltip == null) || (tooltip.equals("")))
		{
			tooltip = "";		
		}
		tooltip = " " + tooltip + " ";

		item.setData("TOOLTIP", tooltip);        

		

		switch (columnIndex) {
		


		case 0:
			return ont.getDisplayName();
		case 1:
			return ont.getVersion();
		case 2:
			return ont.getTableName();
		case 3:
			return ont.getSource();
		case 4:
			return ont.getFileCreationDate();
		case 5:
			return ont.getContact();
		case 6:
			return ont.getComments();

		default:
			return "Should not happen";
		}

	}
}
