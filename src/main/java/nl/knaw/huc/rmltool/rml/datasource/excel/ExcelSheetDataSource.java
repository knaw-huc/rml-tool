package nl.knaw.huc.rmltool.rml.datasource.excel;


import com.google.common.collect.Lists;
import nl.knaw.huc.rmltool.rml.DataSource;
import nl.knaw.huc.rmltool.rml.ErrorHandler;
import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.datasource.JoinHandler;
import nl.knaw.huc.rmltool.rml.datasource.RowFactory;
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
  private final RowFactory rowFactory;
  private final JoinHandler joinHandler;

  public ExcelSheetDataSource(Sheet sheet, RowFactory rowFactory) {
    this.sheet = sheet;
    this.rowFactory = rowFactory;
    joinHandler = rowFactory.getJoinHandler();
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
                              .filter(headerIndex-> row.getCell(headerIndex.getRight()) != null )
                              .map(headerIndex -> tuple(
                                  headerIndex.getLeft(),
                                  row.getCell(headerIndex.getRight()).getStringCellValue()
                              )).collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight));


                          return rowFactory.makeRow(values, errorHandler);
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
    joinHandler.willBeJoinedOn(fieldName, referenceJoinValue, uri, outputFieldName);
  }
}
