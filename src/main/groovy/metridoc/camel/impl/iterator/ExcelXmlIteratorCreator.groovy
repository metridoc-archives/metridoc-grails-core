/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package metridoc.camel.impl.iterator;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ExcelXmlIteratorCreator {

	public static final String CELL = "c";
	public static final String CELL_TYPE = "t";
	public static final String TYPE_STRING = "s";
	public static final String VALUE = "v";
	public static final String ROW = "r";

	public static ArrayList<List> fileContent;

    public ExcelXmlIteratorCreator() {}

	public ArrayList getFileContent(String filename) throws Exception {
		fileContent = new ArrayList();
		processOneSheet(filename);
		
		return fileContent;
	}

	public void processOneSheet(String filename) throws Exception {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader( pkg );
		SharedStringsTable sst = r.getSharedStringsTable();

		XMLReader parser = fetchSheetParser(sst);

		InputStream sheet = r.getSheet("rId1");
		InputSource sheetSource = new InputSource(sheet);

		parser.parse(sheetSource);

		sheet.close();
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
		XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		ContentHandler handler = new SheetHandler(sst);
		parser.setContentHandler(handler);
		return parser;
	}

	/** 
	 * See org.xml.sax.helpers.DefaultHandler javadocs 
	 */
	private static class SheetHandler extends DefaultHandler {
		private SharedStringsTable sst;
		private String lastContents;
		private boolean nextIsString;
		private String cols;
		private int maxCols;
		private int rownum;
		private int colnum;
		private String[] rowContent;
		private SortedMap rowMap;
		private String columnLetter;
		private String[] columns = [ "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z" ];

		private SheetHandler(SharedStringsTable sst) {
			this.sst = sst;
		}

		public void startDocument() {
		    System.out.println("Start document: ");
		}    

		public void endDocument()  {
		    System.out.println("End document: ");
		}
  
		public void startElement(String uri, String localName, String element,
				Attributes attributes) throws SAXException {			
			if (element.equals("row")) {
				cols = attributes.getValue("spans");
				rownum = Integer.parseInt(attributes.getValue(ROW));
				maxCols = Integer.parseInt(cols.substring(cols.indexOf(":")+1));
				colnum = 0;

				// Due to the problem of blank or missing cells at the end of a row... 
				// we need to check to see if we have an unwritten rowMap

				if (rowMap != null && !rowMap.isEmpty()) {
					List mapValues = new ArrayList(rowMap.values());
					System.out.println(mapValues);
					Collections.addAll(fileContent, mapValues); 
				}

				rowMap = new TreeMap();
				for (int i = 0; i < maxCols; i++) {
					rowMap.put(columns[i], "");
				}
			}

			if (element.equals(CELL)) {  // c => cell
				// Figure out if the value is an index in the SST
				String cellType = attributes.getValue(CELL_TYPE);

				// id the column letter
				columnLetter = attributes.getValue(ROW).substring(0,1);

				// check to see if a column has been skipped

				if (cellType != null && cellType.equals(TYPE_STRING)) {
					nextIsString = true;
				} else {
					nextIsString = false;
				}
			}
			
			lastContents = "";   // clears contents cache
		}
		
		public void endElement(String uri, String localName, String element) throws SAXException {
			if (element.equals(VALUE)) {
				if (nextIsString) {
					int idx = Integer.parseInt(lastContents);
					lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
				}
				rowMap.put(columnLetter, lastContents);

				colnum = Arrays.asList(columns).indexOf(columnLetter);

				if (colnum==(maxCols-1))  {
					List mapValues = new ArrayList(rowMap.values());
					System.out.println(mapValues);
					Collections.addAll(fileContent, mapValues); 
					rowMap = null;
				}
			} 
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			lastContents += new String(ch, start, length);
		}

		public void ignorableWhitespace(char[] ch, int start, int length) {
		    System.out.println("Ignorable whitespace: " + new String(ch, start, length));
		}

		public void warning(SAXParseException spe) {
		    System.out.println("Warning at line "+spe.getLineNumber());
		    System.out.println(spe.getMessage());
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			System.out.println("Fatal error at line "+spe.getLineNumber());
		    System.out.println(spe.getMessage());
		    throw spe;
  		}
	}
}

