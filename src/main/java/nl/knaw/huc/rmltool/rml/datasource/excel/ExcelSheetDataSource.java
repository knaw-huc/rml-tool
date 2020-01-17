package nl.knaw.huc.rmltool.rml.datasource.excel;


import com.google.common.collect.Lists;
import nl.knaw.huc.rmltool.rml.DataSource;
import nl.knaw.huc.rmltool.rml.ErrorHandler;
import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.datasource.jexl.JexlRowFactory;
import nl.knaw.huc.rmltool.util.Tuple;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static nl.knaw.huc.rmltool.util.Tuple.tuple;

public class ExcelSheetDataSource implements DataSource {


  private final Sheet sheet;
  private final JexlRowFactory jexlRowFactory;

  public ExcelSheetDataSource(Sheet sheet, JexlRowFactory jexlRowFactory) {
    this.sheet = sheet;
    this.jexlRowFactory = jexlRowFactory;
  }

  @Override
  public Stream<Row> getRows(ErrorHandler errorHandler) {
    final Iterator<org.apache.poi.ss.usermodel.Row> iterator = sheet.iterator();
    final List<String> headers = getHeaders(iterator);

    Iterable<org.apache.poi.ss.usermodel.Row> excelRows = () -> iterator;

    return StreamSupport.stream(excelRows.spliterator(), false)
                        .map(row -> {
                          final Map<String, String> values = headers
                              .stream()
                              .map(header -> tuple(header, headers.indexOf(header)))
                              .map(headerIndex -> tuple(
                                  headerIndex.getLeft(),
                                  row.getCell(headerIndex.getRight()).getStringCellValue()
                              )).collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight));


                          return jexlRowFactory.makeRow(values, errorHandler);
                        });
  }

  private List<String> getHeaders(Iterator<org.apache.poi.ss.usermodel.Row> iterator) {
    final org.apache.poi.ss.usermodel.Row headerRow = iterator.next();
    final List<String> headers = Lists.newArrayList();
    headerRow.cellIterator().forEachRemaining(cell -> headers.add(cell.getStringCellValue()));
    return headers;
  }

  @Override
  public void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName) {
    throw new UnsupportedOperationException("Not yet implemented");//FIXME: implement
  }
}
