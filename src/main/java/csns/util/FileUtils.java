/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012-2014, Chengyu Sun (csun@calstatela.edu).
 * 
 * CSNS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * CSNS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with CSNS. If not, see http://www.gnu.org/licenses/agpl.html.
 */
package csns.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.id.CompositeNestedGeneratedValueGenerator.GenerationContextLocator;

@Component
public class FileUtils {

	public Map<String, List<String>> readExcel(InputStream fis) {
		Map<String, List<String>> result = new HashMap<>();
		String[][] values = null;
		Row row = null;
		Cell cell = null;
		try {
			Workbook wb = WorkbookFactory.create( fis );
			Sheet sheet = wb.getSheetAt( 0 );
			int rows = sheet.getPhysicalNumberOfRows();
			int cells = sheet.getRow( 0 ).getPhysicalNumberOfCells();
			values = new String[rows][cells];

			for ( int r = 0; r < rows; r++ ) {
				row = sheet.getRow( r );
				if ( row != null ) {
					for ( int c = 0; c < cells; c++ ) {
						cell = row.getCell( c );
						values[r][c] = getCellValue( cell );
					}
				}
				System.out.print( "\n" );
			}

			for ( int c = 0; c < cells; c++ ) {
				String key = "";
				for ( int r = 0; r < rows; r++ ) {
					if ( r == 0 ) {
						key = values[r][c].toLowerCase();
						result.put( key, new ArrayList<String>() );
					} else {
						result.get( key ).add( values[r][c] );
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private String getCellValue( Cell cell ) {
		if ( cell != null ) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					return cell.getStringCellValue();
				case Cell.CELL_TYPE_NUMERIC:
					return ((Double) cell.getNumericCellValue()).toString();
			}
		}
		return "";
	}
}
