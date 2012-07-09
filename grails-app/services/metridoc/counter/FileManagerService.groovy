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
package metridoc.counter
/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/8/12
 * Time: 12:16 PM
 */

import org.apache.log4j.Logger

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class FileManagerService {

	private static String REPORT_FILE_NAME_PREFIX = "counter";
	private static String REPORT_FILE_NAME_DELIMITER = "_";
	private static String FILENAME_DATE_FORMAT="MMddyy"
	private static final Logger LOG = Logger.getLogger("counter.FileManager");

	def filesDir = System.getProperty("user.home") + File.separator + "counter" + File.separator + "reports"

	public File[] getCounterFiles(){
		return getFilesSortedByDate(filesDir, true);
	}

	/**
	 * Returns latest report file of specified type for specified year
	 * @param reportType
	 * @param reportYear
	 * @return
	 */
	public File getReportFile(String reportType, int reportYear){
		String fileName = getReportFileName(reportType, reportYear);
		if(fileName != null){
			return new File(filesDir + File.separator + fileName);
		}
		return null;
	}

	/**
	 * Returns latest report file name of specified type for specified year
	 * @param reportType
	 * @param reportYear
	 * @return
	 */
	public String getReportFileName(String reportType, int reportYear){
		final String fileNamePrefix = (REPORT_FILE_NAME_PREFIX + reportType +
		REPORT_FILE_NAME_DELIMITER + reportYear + REPORT_FILE_NAME_DELIMITER).toLowerCase();
		LOG.info("fileNamePrefix is " + fileNamePrefix);
		File counterFilesDir = new File(filesDir);
		String[] fileNames = counterFilesDir.list(new FilenameFilter() {
			public boolean accept(File file, String name) {
				return name.toLowerCase().startsWith(fileNamePrefix);
			}
		});
		return selectFileName(fileNames, fileNamePrefix);
	}
	private static Date getDate(DateFormat dateFormat, String fileName, String fileNamePrefix){
		String datePart = getDatePartFromFileName(fileName, fileNamePrefix);
		Date date = null;
		if(datePart != null){
			try {
				date =  dateFormat.parse(datePart);
			} catch (ParseException e) {
				LOG.error("Cannot parse date part: " + datePart);
			}
		}
		return date;
	}
	private static String selectFileName(String[] fileNames, final String fileNamePrefix){
		if(fileNames != null && fileNames.length > 0){
			//String datePattern = Config.getInstance().getProperty(Config.COUNTER_FILENAME_DATE_FORMAT);
			final SimpleDateFormat dateFormat = new SimpleDateFormat(FILENAME_DATE_FORMAT);
			Arrays.sort(fileNames, new Comparator<String>(){
				public int compare(String fileName1, String fileName2) {
					Date date1 = getDate(dateFormat, fileName1, fileNamePrefix);
					Date date2 = getDate(dateFormat, fileName2, fileNamePrefix);
					if(date1 != null && date2 != null){
						return date1.compareTo(date2)*(-1);
					}else if(date2 != null){
						return 1;
					}else{
						return 0;
					}
				 } });
			return fileNames[0];
		}else{
			return null;
		}
	}

	private static String getDatePartFromFileName(String fileName, String prefix){
		int endIndex = fileName.lastIndexOf(".");
		int startIndex = prefix.length();
		String datePart = null;
		try{
			datePart = fileName.substring(startIndex, endIndex);
		}catch (IndexOutOfBoundsException e) {
			LOG.fine("Cannot extract data part from file name: " + fileName);
		}
		return datePart;
	}

	private static File[] getFilesSortedByDate(String dirPath, boolean desc){
		File counterFilesDir = new File(dirPath);
		File[] files = counterFilesDir.listFiles();
		if(files != null){
			final int i = desc?-1:1;
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File file1, File file2) {
					 long result = file1.lastModified() - file2.lastModified();
					 if (result < 0) {
						 return -1*i;
					 } else if (result > 0) {
						 return 1*i;
					 } else {
						 return 0;
					 }
				 } });
		}
		return files;
	}

}
