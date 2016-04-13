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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.eclipse.UserInfoBean;

import edu.harvard.i2b2.eclipse.plugins.metadataLoader.util.Messages;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.loader.LoaderView;

public class MetadataLoaderView extends ViewPart {

	public static final String ID = "edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.metadataLoaderView";
	public static final String THIS_CLASS_NAME = MetadataLoaderView.class.getName();
	
	//setup context help
	public static final String PREFIX = "edu.harvard.i2b2.eclipse.plugins.metadataLoader";
	public static final String FIND_VIEW_CONTEXT_ID = PREFIX + ".metadata_loader_help_context";
	
	private Log log = LogFactory.getLog(THIS_CLASS_NAME);

	static Composite compositeMapper;

	private static TabItem loaderTab;

	private Button showDisplayButton;

	/**
	 * The constructor.
	 */
	public MetadataLoaderView() {
		
		if (UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax") != null)
			System.setProperty("OntFindMax", UserInfoBean.getInstance().getCellDataParam("ont", "OntFindMax"));
		else 
			System.setProperty("OntFindMax","200");	
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		log.info("Metadata Loader version 1.7.0");
		
		compositeMapper = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
	//	layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		compositeMapper.setLayout(layout);
		
		GridData gridData = new GridData (GridData.FILL_BOTH);
	//	fromTreeGridData.widthHint = 300;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		compositeMapper.setLayoutData(gridData);
	    
		if (UserInfoBean.getInstance().getCellDataMethod("ont") == null){
			
			final Composite notValid = new Composite(compositeMapper, SWT.NONE);

			GridData layoutData = new GridData();
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.horizontalAlignment = GridData.FILL;
			layoutData.grabExcessVerticalSpace = true;
			layoutData.verticalAlignment = GridData.FILL;
			notValid.setLayoutData(layoutData);

			notValid.setLayout(new GridLayout(1, false));


			Label label= new Label(notValid, SWT.NONE | SWT.WRAP);
			label.setText("\n\n"+Messages.getString("MetadataLoaderView.LoadCellNotConfigured"));

			GridData data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			label.setLayoutData(data);

			return;
		}
		

	  

		if (!(Roles.getInstance().isRoleValid()))
		{
			final Composite notValid = new Composite(compositeMapper, SWT.NONE);
			
			GridData layoutData = new GridData();
			layoutData.grabExcessHorizontalSpace = true;
			layoutData.horizontalAlignment = GridData.FILL;
			layoutData.grabExcessVerticalSpace = true;
			layoutData.verticalAlignment = GridData.FILL;
			notValid.setLayoutData(layoutData);
			
			notValid.setLayout(new GridLayout(1, false));
			
			
			Label label= new Label(notValid, SWT.NONE | SWT.WRAP);
			label.setText("\n\n"+Messages.getString("MetadataLoaderView.MinRoleNeeded"));
			
			GridData data = new GridData();
			data.horizontalAlignment = GridData.CENTER;
			data.grabExcessHorizontalSpace = true;
			data.grabExcessVerticalSpace = true;
			label.setLayoutData(data);
					
			showDisplayButton = new Button(notValid, SWT.PUSH);
			showDisplayButton.setText("Display Anyway");

			GridData data1 = new GridData();
			data1.horizontalAlignment = GridData.END;
			data1.grabExcessHorizontalSpace = true;
			data1.grabExcessVerticalSpace = true;
			showDisplayButton.setLayoutData(data1);
			
			showDisplayButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
						if ((notValid != null) && (!notValid.isDisposed())) {
							notValid.dispose();	
							setup(compositeMapper);						
						}
				}
			});
		}
		else 
			setup(compositeMapper);
	}


	private void setup(Composite compositeMapper){

		GridData gridData = new GridData (GridData.FILL_BOTH);
		//	fromTreeGridData.widthHint = 300;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		compositeMapper.setLayoutData(gridData);

		// Create the tab folder
		final TabFolder tabFolder = new TabFolder(compositeMapper, SWT.NONE);
		tabFolder.setLayoutData(gridData);
		//		 Create each tab and set its text, tool tip text,
		// image, and control
		
		// image, and control
		loaderTab = new TabItem(tabFolder, SWT.BOTTOM);
		loaderTab.setText("Metadata Loader");
		loaderTab.setToolTipText("Tool for loading metadata files");
		LoaderView summaryViewer = new LoaderView();
		loaderTab.setControl(summaryViewer.getTabControl(tabFolder));


		// Select the first tab (index is zero-based)
		tabFolder.setSelection(0);
		compositeMapper.layout(true);

		//setup context help
		//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, FIND_VIEW_CONTEXT_ID);
		//		addHelpButtonToToolBar();
	}


	//
	// Passing the focus request
	//
	@Override
	public void setFocus() {
	//	mapperTab.getControl().setFocus();
	}
	
/*
	//add help button
	private void addHelpButtonToToolBar() {
		final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		Action helpAction = new Action(){
			@Override
			public void run() {
				helpSystem.displayHelpResource("/edu.harvard.i2b2.eclipse.plugins.mapperViewer/html/i2b2_map_terms_index.htm");
		}
		};
		helpAction.setImageDescriptor(ImageDescriptor.createFromFile(MapView.class, "/icons/help.png"));
		getViewSite().getActionBars().getToolBarManager().add(helpAction);
	}

*/
}
