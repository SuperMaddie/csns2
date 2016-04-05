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

@Component
public class FileUtils {

	public FileUtils() {
	}

	public Map<String, List<String>> readExcel(InputStream fis) {
		Map<String, List<String>> result = new HashMap<>();
		List<String> columns = new ArrayList<String>();

		try {
			Workbook wb = WorkbookFactory.create(fis);
			int i = 0;
			int j = 0;
			for (Sheet sheet : wb) {
				for (Row row : sheet) {
					j = 0;
					for (Cell cell : row) {
						//cell.setCellType(Cell.CELL_TYPE_STRING);
						String s = getCellValue(cell);
						if (i == 0) {
							result.put(s.toLowerCase(), new ArrayList<String>());
							columns.add(s.toLowerCase());
						} else {
							result.get(columns.get(j)).add(s);
						}
						j++;
					}
					i++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private String getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return ((Double) cell.getNumericCellValue()).toString();
		}
		return "";
	}
}
