/*
 * Copyright (c) 2006-2013 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.widgets.TabFolder;


import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import edu.harvard.i2b2.eclipse.UserInfoBean;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.RunData;
import edu.harvard.i2b2.ontclient.datavo.vdo.GetReturnType;

public class LoaderView extends ApplicationWindow {

	public static final String THIS_CLASS_NAME = LoaderView.class.getName();

	//setup context help
//	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.importBigData";
//	public static final String FIND_VIEW_CONTEXT_ID = PREFIX + ".view_help_context";

	private Log log = LogFactory.getLog(THIS_CLASS_NAME);
	private String catalogLocation = null;

	/**
	 * The constructor.
	 */
	public LoaderView() {
		super(null);
	}

	public Control getTabControl(TabFolder tabFolder) {
		log.info("Metadata Loader view version 1.7.0");
		
		
		Composite summary = new Composite(tabFolder, SWT.NONE);
		summary.setLayout(new GridLayout(1, false));
		
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 1;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		summary.setLayoutData(layoutData);

		
		FormToolkit toolkit = new FormToolkit(tabFolder.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(summary);
		form.setLayoutData(layoutData);
		final Composite body = form.getBody();

		TableWrapLayout wrapLayout = new TableWrapLayout();
		wrapLayout.numColumns = 2;
		body.setLayout(wrapLayout);

		TableWrapData td2 = new TableWrapData(TableWrapData.FILL_GRAB);
		td2.colspan = 2;
		td2.grabHorizontal = true;
		td2.grabVertical = true;
		body.setLayoutData(td2);

		Section section = toolkit.createSection(body, Section.DESCRIPTION
				| Section.TITLE_BAR | SWT.WRAP);

		section.setText("Metadata Loader");
		section.setDescription("This tool allows users to load metadata from an external catalog to the project you are logged into.  \nIt requires this project's table_access and schemes tables to be set up and configured.  \nIt also requires i2b2 hive version 1.7.08 or later.  ");
		section.setLayoutData(td2);

		GridLayout layout1 = new GridLayout(1, false);
		layout1.verticalSpacing = 15;
		layout1.marginWidth = 10;
		layout1.marginHeight = 10;

		Composite loaderComp = toolkit.createComposite(section, SWT.WRAP);
		loaderComp.setLayout(layout1);
		loaderComp.setLayoutData(layoutData);
		
		section.setClient(loaderComp);

		GridLayout layout3 = new GridLayout(3, false);
		layout3.verticalSpacing = 15;
		layout3.marginWidth = 10;
		layout3.marginHeight = 10;

		Composite headerComp = toolkit.createComposite(loaderComp, SWT.WRAP);
		headerComp.setLayout(layout3);
		headerComp.setLayoutData(layoutData);
		
		
		toolkit.createLabel(headerComp, "Catalog file location:", SWT.RIGHT);
		final Text catalogText = new Text(headerComp, SWT.SINGLE | SWT.BORDER);
		catalogText.setLayoutData(layoutData);
		Button refresh = toolkit.createButton(headerComp, "Refresh", SWT.PUSH);
		catalogText.setText(
				"https://raw.githubusercontent.com/i2b2plugins/i2b2-ontology-catalog/master/ontologyCatalog.xml");

		catalogText.addSelectionListener(new SelectionListener(){

			public void widgetSelected(SelectionEvent e) {
				catalogLocation = catalogText.getText();
				RunData.getInstance().setCatalogLocation(catalogLocation);
				TableComposite.getInstance().update();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				catalogLocation = catalogText.getText();
				RunData.getInstance().setCatalogLocation(catalogLocation);
				TableComposite.getInstance().update();
			}
		});

		catalogText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {	    
				// Text Item has been entered
				// Does not require 'return' to be entered
				catalogLocation = catalogText.getText();
				RunData.getInstance().setCatalogLocation(catalogLocation);
			}
		});
		
		
		toolkit.createLabel(loaderComp, "Available ontologies to load:", SWT.WRAP);
		TableComposite.setInstance(loaderComp);
	
		refresh.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				catalogLocation = catalogText.getText();
				RunData.getInstance().setCatalogLocation(catalogLocation);
				TableComposite.getInstance().clear();
				TableComposite.getInstance().update();
				TableComposite.getInstance().refresh();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {


			}
		});
		

		form.reflow(true);
		
		return summary;
	}




	}

