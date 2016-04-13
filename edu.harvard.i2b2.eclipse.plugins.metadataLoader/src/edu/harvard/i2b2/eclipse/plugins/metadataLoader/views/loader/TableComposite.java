/*
 * Copyright (c) 2006-2016 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 	     Lori Phillips
 */
package edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBElement;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import edu.harvard.i2b2.common.exception.I2B2Exception;
import edu.harvard.i2b2.common.util.jaxb.JAXBUtilException;

import edu.harvard.i2b2.eclipse.ICommonMethod;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.model.MetadataContentProvider;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.model.MetadataLabelProvider;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.model.MetadataLoaderType;

import edu.harvard.i2b2.eclipse.plugins.metadataLoader.util.CSVFileReader;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.util.FileUtil;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.util.MetadataLoaderJAXBUtil;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.RunData;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.ws.OntServiceDriver;
import edu.harvard.i2b2.eclipse.plugins.metadataLoader.ws.OntologyResponseMessage;

import edu.harvard.i2b2.ontclient.datavo.i2b2message.StatusType;
import edu.harvard.i2b2.ontclient.datavo.vdo.MetadataLoadType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyCatalogType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyDataType;
import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyLoadType;


public class TableComposite 
{
	private Log log = LogFactory.getLog(TableComposite.class.getName());	
	private Table table;

	private TableViewer viewer;
	private List<OntologyLoadType> list = null;
	private static TableComposite instance;

	private Label step1Status = null;

	private Label step2Status = null;
	private Thread runningThread = null;
	
	public TableComposite(Composite parent)
	{
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		
		tableComposite.setLayout(new GridLayout(1, false));
		
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		tableComposite.setLayoutData(layoutData);
	
		
		viewer = new TableViewer(tableComposite, SWT.CHECK 
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		
		createColumns(viewer);
		viewer.setContentProvider(new MetadataContentProvider());
		viewer.setLabelProvider(new MetadataLabelProvider(viewer, Display.getCurrent()));
		
		final Button loadButton = new Button(tableComposite, SWT.PUSH);		
		loadButton.setText("Load selected ontology");
    	final Button cancelButton = new Button(tableComposite, SWT.PUSH);
    	cancelButton.setText("Cancel load");
    	cancelButton.setEnabled(false);
    	
    	GridData gridData = new GridData (GridData.FILL_HORIZONTAL);
		gridData.widthHint = 300;
		gridData.heightHint = 25;
		gridData.horizontalSpan = 1;
		
		Label step1Header = new Label(tableComposite, SWT.LEFT);
		step1Header.setLayoutData(gridData);
		step1Header.setText("");

		step1Status = new Label(tableComposite, SWT.LEFT | SWT.BOLD);
		step1Status.setLayoutData(gridData);
		step1Status.setText("");

		step2Status = new Label(tableComposite, SWT.LEFT | SWT.BOLD);
		step2Status.setLayoutData(gridData);
		step2Status.setText("");
	
		loadButton.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean prevInstalledFlag = false;
				
				List<OntologyLoadType> ontologies = new ArrayList();
				
				// This is a bypass of not being able to disable the checkbox for previously installed ontologies
				// If any are previously installed , removed them from list to install 
				
				for(int i=0; i < viewer.getTable().getItemCount(); i++){					
					OntologyLoadType loadOntology = (OntologyLoadType)viewer.getElementAt(i);
					
					if( viewer.getTable().getItem(i).getChecked()) {
						if(loadOntology.getComments() == null)
							ontologies.add(loadOntology);
						else if(!(loadOntology.getComments().contains("installed")))
							ontologies.add(loadOntology);
						else
							prevInstalledFlag = true;
					}
					
				}
				
				if(prevInstalledFlag == true){
					MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Previously installed ontologies will not be loaded again ");
					int result = mBox.open();	
				}
				
				if(ontologies.size() < 1)
					return;

				cancelButton.setEnabled(true);
				step2Status.setText("");
                
				step1Status.setText("Initiating metadata load");
				
				log.info("load starting..");	
				loadButton.setEnabled(false);

				try {

					// kick off process:
					//get the file at the url
					// unzip the file
					// load table_access data (if table_cd or table is already there then stop
					// load schemes
					// load metadata in chunks, send table name also.


					runningThread = loadIt(ontologies, loadButton, cancelButton, step1Status, step2Status, Display.getCurrent(), list, viewer);
					runningThread.setName("start");
					runningThread.start();

				} catch (Exception e1) {
					step1Status.setText("Metadata load failed: " + e1.getMessage());
					cancelButton.setEnabled(false);
					loadButton.setEnabled(true);

				}
				

			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {


			}
		});


		cancelButton.addSelectionListener(new SelectionListener(){    		
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox mBox1 = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				mBox1.setText("Cancel Process Request");
				mBox1.setMessage("Are you sure you want to cancel the metadata load? ");
				int result1 = mBox1.open();
			if(result1 == SWT.NO)
				return;
			
			loadButton.setEnabled(true);
			cancelButton.setEnabled(false);
			
			if((runningThread != null) || ((runningThread.isAlive()))){
				runningThread.setName("stop");
				
			}
			step1Status.setText("Cancelling");

			MessageBox mBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK);
			mBox.setText("Please Note ...");
			mBox.setMessage("It can take a moment for the process to be completely cancelled " +
					"\n Please wait before starting another process ");
			int result = mBox.open();
			
		}	

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	});
    	
	}

	public static void setInstance(Composite composite) {
		instance = new TableComposite(composite);
	}

	/**
	 * Function to return the TableComposite instance
	 * 
	 * @return  TableComposite object
	 */
	public static TableComposite getInstance() {
		return instance;
	}
	


	private void createColumns(TableViewer viewer) {

		String[] titles = {"Names", "Version", "Table", "Source", "Date", "Contact", "Comments"};
		int[] bounds = { 250, 75, 150, 75, 100, 250, 300};

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);

		}
		table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		gridData1.grabExcessVerticalSpace = true;
		gridData1.heightHint = 250;
		table.setLayoutData(gridData1);


	}

	public void refresh()
	{
		this.viewer.getTable().clearAll();
		this.viewer.refresh();
		if(list.isEmpty()){
			OntologyLoadType ont = new OntologyLoadType();
			ont.setDisplayName("Catalog is empty");
			list.add(ont);
		}
		this.viewer.setInput(list);
		this.viewer.refresh();
	}
	
	public void clear(){
		this.viewer.getTable().clearAll();
		this.viewer.refresh();
	}

	public List<OntologyLoadType> getList(){
		return list;
	}

	public void update(){
		String catalogString;
		try {
			// for a local file
	//		FileInputStream catalogStream = new FileInputStream(new File(RunData.getInstance().getCatalogLocation()));
		//	FileInputStream catalogStream = new FileInputStream((RunData.getInstance().getCatalogLocation()));
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(RunData.getInstance().getCatalogLocation());
	
			HttpResponse response = httpclient.execute(httpget);

			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			if (entity != null) {

				InputStream catalogStream = entity.getContent();


				catalogString = IOUtils.toString(catalogStream, "UTF-8");
				JAXBElement jaxbElement = MetadataLoaderJAXBUtil.getJAXBUtil().unMashallFromString(catalogString);
				OntologyCatalogType catalog  = (OntologyCatalogType) jaxbElement.getValue();
				list = catalog.getOntology();
			}
			httpclient.getConnectionManager().shutdown();
			
			// for a local file
		//	catalogString = IOUtils.toString(catalogStream, "UTF-8");
		//	JAXBElement jaxbElement = MetadataLoaderJAXBUtil.getJAXBUtil().unMashallFromString(catalogString);
		//	OntologyCatalogType catalog  = (OntologyCatalogType) jaxbElement.getValue();
		//	list = catalog.getOntology();
			checkIfPreviouslyInstalled(list, Display.getCurrent(), this.viewer);
			// check to see if any tables previously installed
			
			refresh();
			
		} catch (FileNotFoundException e) {
			Display.getCurrent().syncExec(new Runnable() {
				TableViewer theViewer = viewer;
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTable().getShell(), SWT.ERROR | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Catalog not found at location \n Please make sure the location is correct and try again");
					int result = mBox.open();
			//		TableComposite.getInstance().refresh();
				}
			});
		} catch (JAXBUtilException e) {
			Display.getCurrent().syncExec(new Runnable() {
				TableViewer theViewer = viewer;
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTable().getShell(), SWT.ERROR | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("Catalog not in correct format");
					int result = mBox.open();
			//		TableComposite.getInstance().refresh();
				}
			});
		
		} catch (IOException e) {
			Display.getCurrent().syncExec(new Runnable() {
				TableViewer theViewer = viewer;
				public void run() {
					// e.getMessage() == Incoming message input stream is null  -- for the case of connection down.
					MessageBox mBox = new MessageBox(theViewer.getTable().getShell(), SWT.ERROR | SWT.OK);
					mBox.setText("Please Note ...");
					mBox.setMessage("An I/O exception occurred \n Please make sure the location is correct and try again");
					int result = mBox.open();
			//		TableComposite.getInstance().refresh();
				}
			});
		}
		
	}

	public void checkIfPreviouslyInstalled(List list, Display display, TableViewer viewer) {
		OntologyResponseMessage msg = new OntologyResponseMessage();
		
		try {
			for (int i=0; i< list.size(); i++){
				OntologyLoadType ont = (OntologyLoadType) list.get(i);
				MetadataLoadType metadata = new MetadataLoadType();
				metadata.setTableName(ont.getTableName());
				String response = OntServiceDriver.checkForTableExistence(metadata,"ONT");
				// look at response for error

				// checks to see if table already installed..
				StatusType procStatus = msg.processResult(response);
				if (procStatus.getType().equals("ERROR")){
					if(procStatus.getValue().equals("Previously installed")){
						ont.setComments(procStatus.getValue());
					//	viewer.getTable().getItem(i).setChecked(false);
					//	viewer.getTable().getItem(i).setGrayed(true);
					}	
				}
			}

		} catch (I2B2Exception e) {

			return;
		} catch (Exception e) {

			return;
		}


	}
	
	
	public Thread loadIt(List loadOntologies,Button loadButton, Button cancelButton, Label status1,  Label status2,  Display display, List list, TableViewer viewer) {
		final Button theButton = loadButton;
		final Button theCancelButton = cancelButton;
		final Label theStatus = status1;
		final Label theStatus2 = status2;
		final Display theDisplay = display;
		final List theLoadOntologies = loadOntologies;
		final List theList = list;
		final TableViewer theViewer = viewer;

		return new Thread() {
			public void run(){
				this.setName("start");
				final Thread thisThread = this;
				// first unzip thefile
				
				// eclipse sdk config
				String workingDir = System.getProperty("user.dir");
				if(workingDir.contains("\\"))
					workingDir = workingDir.replace("\\","/");
				workingDir = workingDir.substring(0, workingDir.lastIndexOf("/")) ;
				
				
				// comment out next line for standalone config
				workingDir = workingDir + "/edu.harvard.i2b2.eclipse.plugins.metadataLoader/";		
				for (int i = 0; i < theLoadOntologies.size(); i++){
					final OntologyLoadType theLoadOntology = (OntologyLoadType)(theLoadOntologies.get(i));
					if(this.getName().equals("stop"))
						return;
					try {
						FileUtil.download(workingDir, ((OntologyLoadType)(theLoadOntologies.get(i))).getURL(), ((OntologyLoadType)(theLoadOntologies.get(i))).getFileName());
						FileUtil.unzip(workingDir + ((OntologyLoadType)(theLoadOntologies.get(i))).getFileName());
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theStatus.setText("Extracted files");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Extracted files");

							}
						});
					} catch (IOException e) {
						final String errorMsg = e.getMessage();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theButton.setEnabled(true);
								theCancelButton.setEnabled(false);
								theStatus.setText("Metadata file attempting to load not found; exiting");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Metadata file attempting to load not found; exiting");
								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments("File not found: " + ((OntologyLoadType)theList.get(i)).getFileName());
										 theViewer.refresh();
										 break;
									}
								}
							}
						});
				//		return;


					} catch (Exception e) {
						final String errorMsg = e.getMessage();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theButton.setEnabled(true);
								theCancelButton.setEnabled(false);
								theStatus.setText("Problem extracting the metadata file to load; exiting");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Problem extracting the metadata file to load; exiting");

								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments("Problem extracting file: " + ((OntologyLoadType)theList.get(i)).getFileName());
										 theViewer.refresh();
										 break;
									}
								}
							}
						});

				//		return;
					}


					try {
						processTableAccess();

						processSchemes();
						theDisplay.syncExec(new Runnable() {
							public void run() {

								theStatus.setText("Processed table_access and schemes");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Loaded table_access and schemes");

							}
						});

						processMetadata(theStatus,  theStatus2, theDisplay, thisThread);

						theDisplay.syncExec(new Runnable() {
							
							public void run() {
								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments("installed");
										 theViewer.refresh();
										 break;
									}
								}
								theStatus.setText("Completed load of table " + RunData.getInstance().getMetadataTable() );
								theStatus2.setText("");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Metadata table load complete");
								
								try {
									IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
									IWorkbenchPage[] pages = windows[0].getPages();
								
									final ViewPart navTermsView = (ViewPart) pages[0].findView("edu.harvard.i2b2.eclipse.plugins.ontology.views.ontologyView");			
									pages[0].activate(navTermsView);
									
									((ICommonMethod) navTermsView).doSomething("refresh");
							
									
								} catch (Exception e) {
									
									System.out.println("Could not update Navigate Terms View: " + e.getMessage());
								}

							}
						});

					} catch (IOException e) {
						final String errorMsg = e.getMessage();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theButton.setEnabled(true);
								theCancelButton.setEnabled(false);
								theStatus.setText(errorMsg);
								theStatus2.setText("");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Problem processing metadata file; exiting");
								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments("Problem processing metadata file: " + ((OntologyLoadType)theList.get(i)).getFileName());
										 theViewer.refresh();
										 break;
									}
								}
							}
						});

					//	return;
					} catch (I2B2Exception e) {
						final String errorMsg = e.getMessage();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theButton.setEnabled(true);
								theCancelButton.setEnabled(false);
								theStatus.setText(errorMsg);
								theStatus2.setText("");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage(errorMsg + "; exiting");
								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments(errorMsg);
										 theViewer.refresh();
										 break;
									}
								}
							}
						});

					
					} catch (Exception e) {
						final String errorMsg = e.getMessage();
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theButton.setEnabled(true);
								theCancelButton.setEnabled(false);
								theStatus.setText(errorMsg);
								theStatus2.setText("");
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage("Problem processing metadata file; exiting");
								for(int i=0; i < theList.size(); i++){
									if(((OntologyLoadType)theList.get(i)).getFileName().equals(theLoadOntology.getFileName())){
										((OntologyLoadType)theList.get(i)).setComments("Problem processing metadata file");
										 theViewer.refresh();
										 break;
									}
								}
							}
						});

					}

				}
				
				theDisplay.syncExec(new Runnable() {
					
					public void run() {
						theButton.setEnabled(true);
						theCancelButton.setEnabled(false);
					

					}
				});
			}
		};
		

	}
	
	public void processTableAccess() throws Exception{
		OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;
		CSVFileReader reader = null;
		try {
			String file = RunData.getInstance().getTableAccessFile();
			reader = new CSVFileReader(RunData.getInstance().getTableAccessFile(), '|', '"');
			// Read in header..
			reader.readFields();

			int count = 0;
			int start = 1;
			int end = 10;
			MetadataLoadType categories =	new MetadataLoadType();
			categories.setTableName("TABLE_ACCESS");
			Vector<String> fields = null;
			while(count < end){
				try {
					fields = reader.readFields();
					if((fields == null) || (fields.isEmpty())){
						if(categories.getMetadata().isEmpty())
							break;
						System.out.println("Loading table_access records " + start + " to " + count);

						
						String response = OntServiceDriver.loadMetadata(categories, "ONT");
						// look at response for error
						
						// This one loads data reached when end of file found.
						// This is call to ONT to load the table access data.
						 procStatus = msg.processResult(response);
						if (procStatus.getType().equals("ERROR")){
							System.setProperty("errorMessage", procStatus.getValue());
							reader.close();
							System.out.println("Error loading table_access records: exiting");
							I2B2Exception e = new I2B2Exception(procStatus.getValue());
							throw e;
						}
						break;
					}
				}catch (Exception e) {
					reader.close();
					System.out.println("Error loading table_access records: exiting");
					I2B2Exception e2 = new I2B2Exception("Error loading table_access records; exiting: " + e.getMessage());
					throw e2;
				} 

				if(fields.size() < 23){
					System.out.println("problem; too few fields.");
					I2B2Exception e2 = new I2B2Exception("Error processing table_access records: too few fields in file; exiting ");
					throw e2;
			
					
				}
				
				// This design assumes that the table_access file only lists one metadata table.
				/// It can have multiple rows, but can only refer to one table to create/load.
				
				String metadataTable = fields.get(1);
				if (metadataTable == null){
					reader.close();
					System.out.println("No metadata table specified in table access record; quitting.");
					break;
				}
				
				RunData.getInstance().setMetadataTable(fields.get(1));
				MetadataLoaderType category = new MetadataLoaderType();
				OntologyDataType data = category.fromTableAccess(fields);
				categories.getMetadata().add(data);
				count++;
				// This one loads data reached after we read in a "chunk" of data..
				if(end == count){
					System.out.println("Loading table_access records " + start + " to " + end);
					try {
						String response = OntServiceDriver.loadMetadata(categories, "ONT");

						 procStatus = msg.processResult(response);
							if (procStatus.getType().equals("ERROR")){
								System.setProperty("errorMessage", procStatus.getValue());
								reader.close();
								System.out.println("Error loading table_access records: exiting");
								I2B2Exception e = new I2B2Exception(procStatus.getValue());
								throw e;
							}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						reader.close();
						System.out.println("Error loading table_access records: exiting");
						I2B2Exception e2 = new I2B2Exception("Error loading table_access records; exiting: " + e.getMessage());
						throw e2;
						
					}
					categories.getMetadata().clear();
				//	list.clear();
					end += 10;
					start += 10;
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			I2B2Exception e2 = new I2B2Exception("Error loading table_access records; exiting: " + e1.getMessage());
			throw e2;
		} catch (IOException e1) {
			I2B2Exception e2 = new I2B2Exception("Error loading table_access records; exiting: " + e1.getMessage());
			throw e2;
		}

		System.out.println("Table access loader complete");
		
		
	}
	
	public void processSchemes() throws Exception{
		OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;
		CSVFileReader reader = null;
		try {
			String file = RunData.getInstance().getSchemesFile();
			reader = new CSVFileReader(RunData.getInstance().getSchemesFile(), '|', '"');
			// Read in header..
			reader.readFields();

			int count = 0;
			int start = 1;
			int end = 10;
			MetadataLoadType schemes =	new MetadataLoadType();
			schemes.setTableName("SCHEMES");
			Vector<String> fields = null;
			while(count < end){
				try {
					fields = reader.readFields();
					if((fields == null) || (fields.isEmpty())){
						if(schemes.getMetadata().isEmpty())
							break;
						System.out.println("Loading scheme records " + start + " to " + count);
				
					
						String response = OntServiceDriver.loadMetadata(schemes, "ONT");
						
						procStatus = msg.processResult(response);
						if (procStatus.getType().equals("ERROR")){
							System.setProperty("errorMessage", procStatus.getValue());
							reader.close();
							System.out.println("Error loading schemes records: exiting");
							break;
						}
						
						break;
					}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					reader.close();
					System.out.println("Error loading scheme records: exiting");
					System.exit(1);
				} 

				if(fields.size() < 3){
					System.out.println("problem; too few fields.");
					I2B2Exception e2 = new I2B2Exception("Error processinging schemes records: too few fields in file; exiting ");
					throw e2;
				}

			
				MetadataLoaderType scheme= new MetadataLoaderType();
				schemes.getMetadata().add(scheme.fromSchemes(fields));
				count++;
				if(end == count){
					System.out.println("Loading scheme records " + start + " to " + count);
					try {
						String response = OntServiceDriver.loadMetadata(schemes, "ONT");
						procStatus = msg.processResult(response);
						if (procStatus.getType().equals("ERROR")){
							System.setProperty("errorMessage", procStatus.getValue());
							reader.close();
							System.out.println("Error loading schemes records: exiting");
							break;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						reader.close();
						System.out.println("Error loading scheme records: exiting");
						System.exit(1);
					}
				//	list.clear();
					end += 10;
					start += 10;
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Schemes loader complete");



	}

	public void processMetadata(final Label theStatus,  final Label theStatus2, final Display theDisplay, final Thread thisThread) throws Exception{
		// pass data and metadata table name.
		OntologyResponseMessage msg = new OntologyResponseMessage();
		StatusType procStatus = null;
		CSVFileReader reader = null;
		try {
			String file = RunData.getInstance().getMetadataFile();
			reader = new CSVFileReader(RunData.getInstance().getMetadataFile(), '|', '"');
			// Read in header..
			reader.readFields();

			int count = 0;
			int start = 1;
			int end = 1000;
			MetadataLoadType metadata =	new MetadataLoadType();
			metadata.setTableName(RunData.getInstance().getMetadataTable());
			Vector<String> fields = null;
			while(count < end){
				try {
					if(thisThread.getName().equals("stop")){
						reader.close();
						I2B2Exception e2 = new I2B2Exception("User cancelled load of table " + metadata.getTableName() );
						throw e2;
					}
					fields = reader.readFields();
					if((fields == null) || (fields.isEmpty())){
						if(metadata.getMetadata().isEmpty())
							break;
						System.out.println("Loading " + metadata.getTableName() + " metadata records " + start + " to " + count);
						final String theMsg = "Loading " + metadata.getTableName() + " metadata records " + start + " to " + count;
						
						theDisplay.syncExec(new Runnable() {
							public void run() {
								theStatus2.setText(theMsg);
								IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
										.getActiveWorkbenchWindow()).getActionBars();
								bars.getStatusLineManager().setMessage(theMsg);

							}
						});
						
						String response = OntServiceDriver.loadMetadata(metadata, "ONT");
						
						procStatus = msg.processResult(response);
						if (procStatus.getType().equals("ERROR")){
							System.setProperty("errorMessage", procStatus.getValue());
							reader.close();
							System.out.println("Error loading metadata records: exiting");
							I2B2Exception e = new I2B2Exception(procStatus.getValue());
							throw e;
						}
						// This one loads data reached when end of file found.
						//			String response = MapperServiceDriver.getUnmappedTerms(unmap);
						//persistDao.batchUpdateMetadata(dbInfo, list);
						// This is call to ONT to load the table access data.

						break;
					}
				}catch (Exception e) {
					// TODO Auto-generated catch block
					reader.close();
					System.out.println("Error loading metadata records: exiting");
					I2B2Exception e2 = new I2B2Exception("Error loading metadata records for table " + metadata.getTableName() + "; exiting: " + e.getMessage());
					throw e2;
				} 

				if(fields.size() < 25){
					System.out.println("problem; too few fields.");
					I2B2Exception e2 = new I2B2Exception("Error processing metadata records: too few fields in file; exiting ");
					throw e2;
				}
			
				MetadataLoaderType term = new MetadataLoaderType();
				metadata.getMetadata().add(term.fromMetadata(fields));
				count++;
				if(end == count){
					System.out.println("Loading metadata records " + start + " to " + count);
					
					final String theMsg = "Loading " + metadata.getTableName() + " metadata records " + start + " to " + count;
					
					theDisplay.syncExec(new Runnable() {
						public void run() {
							theStatus2.setText(theMsg);
							IActionBars bars = ((WorkbenchWindow) PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow()).getActionBars();
							bars.getStatusLineManager().setMessage(theMsg);

						}
					});
					
					try {
						String response = OntServiceDriver.loadMetadata(metadata, "ONT");
						
						procStatus = msg.processResult(response);
						if (procStatus.getType().equals("ERROR")){
							System.setProperty("errorMessage", procStatus.getValue());
							reader.close();
							System.out.println("Error loading metadata records: exiting");
							I2B2Exception e = new I2B2Exception(procStatus.getValue());
							throw e;
						}
						//persistDao.batchUpdateMetadata(dbInfo, list);
						// This is call to ONT to load the table access data.
						// This one loads the incremental chunks of data.
					} catch (Exception e) {
						// TODO Auto-generated catch block
						reader.close();
						System.out.println("Error loading metadata records: exiting");
						I2B2Exception e2 = new I2B2Exception("Error loading metadata records for table " + metadata.getTableName() + "; exiting: " + e.getMessage());
						throw e2;
					}
					metadata.getMetadata().clear();
					end += 1000;
					start += 1000;
					if(thisThread.getName().equals("stop")){
						reader.close();
						I2B2Exception e2 = new I2B2Exception("User cancelled load of table " + metadata.getTableName() );
						throw e2;
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Metadata loader complete");



	}


	private class RefreshAction extends Action 
	{
		public RefreshAction()
		{
			super("Refresh");

		}
		@Override
		public void run()
		{
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection.size() != 1)
				return;

			refresh();
		}
	}

} 







