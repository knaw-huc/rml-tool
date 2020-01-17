package nl.knaw.huc.rmltool.rml.datasource.excel;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huc.rmltool.rml.LoggingErrorHandler;
import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.datasource.jexl.JexlRowFactory;
import nl.knaw.huc.rmltool.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExcelDataSourceTest {


  @Test
  public void returnsAllTheCellsInARow() throws Exception {
    final FileInputStream excelFile =
        new FileInputStream(new File(getResource(ExcelDataSourceTest.class, "simplesheet.xlsx").toURI()));
    final Workbook workbook = new XSSFWorkbook(excelFile);
    final JexlRowFactory jexlRowFactory = new JexlRowFactory(Maps.newHashMap(), new HashMapBasedJoinHandler());
    final ExcelSheetDataSource instance = new ExcelSheetDataSource(workbook.getSheetAt(0), jexlRowFactory);

    final List<Row> rows = Lists.newArrayList(instance.getRows(new LoggingErrorHandler()).iterator());

    assertThat(rows, Matchers.hasSize(2));
    assertThat(rows.get(0).getRawValue("Column1"), is("Value1.1"));
    assertThat(rows.get(0).getRawValue("Column2"), is("Value1.2"));
    assertThat(rows.get(1).getRawValue("Column1"), is("Value2.1"));
    assertThat(rows.get(1).getRawValue("Column2"), is("Value2.2"));
  }

}
