package stockretrieval;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public final class ExcelReader {

	//Prevent from making an instance
	private ExcelReader(){
	}
	
	//This is compatible with Excel 2003 or lower version (not compatible with 2007 xml version)
	public static List<List<String>> retrieveExcelFile(String filename, Integer sheetNumber) throws Exception {
		List<List<String>> rowList = new ArrayList<List<String>>();
		FileInputStream file = new FileInputStream(filename);
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(sheetNumber != null ? sheetNumber.intValue() : 0);
		
		//Iterate through every row
		Iterator<Row> rows = sheet.rowIterator();
		while(rows.hasNext()){
			HSSFRow row = (HSSFRow) rows.next();
			Iterator<Cell> columns = row.cellIterator();
			List<String> columnList = new ArrayList<String>();
			
			//Iterate through every column
			while(columns.hasNext()){
				HSSFCell cell = (HSSFCell) columns.next();
				//Add column to the list
				columnList.add(cell.toString());
			}
			//Add columns to rows
			rowList.add(columnList);
		}
		
		return rowList;
	}
}
