/*
* Copyright (c) 2006-2011 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
* are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 *     Lori Phillips - initial API and implementation
 */
package edu.harvard.i2b2.eclipse.plugins.metadataLoader.views.loader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.harvard.i2b2.ontclient.datavo.vdo.OntologyLoadType;


public class CellEditingSupport extends EditingSupport {
		private CellEditor editor;
		private int column;

		public CellEditingSupport(ColumnViewer viewer, int column) {
			super(viewer);

			// Create the correct editor based on the column index
			switch (column) {
			case 0:
				editor = new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
				break;
			default:
				editor = null;
				//				editor = new TextCellEditor(((TableViewer) viewer).getTable());
			}
			this.column = column;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			OntologyLoadType ont = (OntologyLoadType) element;
			switch (this.column) {
			case 0:
				return ont.isEnabled();
			default:
				break;
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {



			switch (this.column) {

			case 0:
				OntologyLoadType ont = (OntologyLoadType) element;
				ont.setEnabled((Boolean) value);
	
				break;
			default:
				break;
			}

			getViewer().update(element, null);
		}


	

	


}
