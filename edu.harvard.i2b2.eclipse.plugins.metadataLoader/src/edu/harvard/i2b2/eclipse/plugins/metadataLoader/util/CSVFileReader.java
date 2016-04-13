package edu.harvard.i2b2.eclipse.plugins.metadataLoader.util;

import java.util.*;
import java.io.*;

/**
 * CSVFileReader is a class derived from CSVFile used to parse an existing CSV file.
 * <p>
 * Adapted from a C++ original that is Copyright (C) 1999 Lucent Technologies.<br>
 * Excerpted from 'The Practice of Programming' by Brian Kernighan and Rob Pike.
 * <p>
 * Included by permission of the <a href="http://tpop.awl.com/">Addison-Wesley</a> web site, which says:
 * <cite>"You may use this code for any purpose, as long as you leave the copyright notice and book citation attached"</cite>.
 *
 * @author  Brian Kernighan and Rob Pike (C++ original)
 * @author  Ian F. Darwin (translation into Java and removal of I/O)
 * @author  Ben Ballard (rewrote handleQuotedField to handle double quotes and for readability)
 * @author  Fabrizio Fazzino (added integration with CSVFile, handling of variable textQualifier and Vector with explicit String type)
 * @version %I%, %G%
 */
public class CSVFileReader extends CSVFile {
	/**
	 * The buffered reader linked to the CSV file to be read.
	 */
  protected BufferedReader in;

	/**
	 * CSVFileReader constructor just need the name of the existing CSV file that will be read.
	 *
	 * @param  inputFileName         The name of the CSV file to be opened for reading
	 * @throws FileNotFoundException If the file to be read does not exist
	 */
  public CSVFileReader(String inputFileName) throws FileNotFoundException {
    super();
    in = new BufferedReader(new FileReader(inputFileName));
  }

	/**
	 * CSVFileReader constructor with a given field separator.
	 *
	 * @param  inputFileName          The name of the CSV file to be opened for reading
	 * @param  sep                    The field separator to be used; overwrites the default one
	 * @throws FileNotFoundException  If the file to be read does not exist
	 */
  public CSVFileReader(String inputFileName, char sep) throws FileNotFoundException {
    super(sep);
    in = new BufferedReader(new FileReader(inputFileName));
  }

	/**
	 * CSVFileReader constructor with given field separator and text qualifier.
	 *
	 * @param  inputFileName          The name of the CSV file to be opened for reading
	 * @param  sep                    The field separator to be used; overwrites the default one
	 * @param  qual                   The text qualifier to be used; overwrites the default one
	 * @throws FileNotFoundException  If the file to be read does not exist
	 */
  public CSVFileReader(String inputFileName, char sep, char qual) throws FileNotFoundException {
    super(sep, qual);
    in = new BufferedReader(new FileReader(inputFileName));
  }

	/**
	 * Split the next line of the input CSV file into fields.
	 * <p>
	 * This is currently the most important function of the package.
	 *
	 * @return             Vector of strings containing each field from the next line of the file
	 * @throws IOException If an error occurs while reading the new line from the file
	 */
  public Vector<String> readFields() throws IOException {
    Vector<String> fields = new Vector<String>();
    StringBuffer sb = new StringBuffer();
    String line = in.readLine();
    if(line==null) return null;

    if(line.length()==0) {
    	return null;
  //    fields.add(line);
  //    return fields;
    }

    int i = 0;
    do {
      sb.setLength(0);
      if(i<line.length() && line.charAt(i)==textQualifier) {
        i = handleQuotedField(line, sb, ++i);  // skip quote
        while(i == -1){  // we hit a multi-line string
        	line = in.readLine();
        	i = handleQuotedField(line, sb, -1);
        }
        	
      } else {
        i = handlePlainField(line, sb, i);
      }
      fields.add(sb.toString());
      i++;
    } while(i<line.length());

    return fields;
  }

	/**
	 * Close the input CSV file.
	 *
	 * @throws IOException If an error occurs while closing the file
	 */
  public void close() throws IOException {
    in.close();
  }

	/**
	 * Handles a quoted field.
	 *
	 * @return index of next separator
	 */
  protected int handleQuotedField(String s, StringBuffer sb, int i) {
    int j;
    int len = s.length();
    
    if(i == -1){
    	sb.append(" \n ");
    	i++;
    }
    for(j=i; j<len; j++) {
      if((s.charAt(j)==textQualifier) && (j+1<len)) {
//        if(s.charAt(j+1) == textQualifier) {
 //         j++;  // skip escape char
//	      } else 
    	  if(s.charAt(j+1)==fieldSeparator) {  // next delimiter
          j++;  // skip end quotes
          break;
        }
      }	else if((s.charAt(j)==textQualifier) && (j+1==len)) {  // end quotes at end of line
        break;  // done
      } else if(j+1 == len){ // we didnt get the 2nd text qualifier before end of line
    	return -1;  
      }
      sb.append(s.charAt(j));  // regular character
    }
    return j;
  }

	/**
	 * Handles an unquoted field.
	 *
	 * @return index of next separator
	 */
  protected int handlePlainField(String s, StringBuffer sb, int i) {
    int j = s.indexOf(fieldSeparator, i);  // look for separator
    if(j == -1) {  // none found
      sb.append(s.substring(i));
      return s.length();
    } else {
      sb.append(s.substring(i, j));
      return j;
    }
  }
}
