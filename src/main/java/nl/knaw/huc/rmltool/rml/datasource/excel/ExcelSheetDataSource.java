package nl.knaw.huc.rmltool.rml.datasource.excel;


import com.google.common.collect.Lists;
import nl.knaw.huc.rmltool.rml.DataSource;
import nl.knaw.huc.rmltool.rml.ErrorHandler;
import nl.knaw.huc.rmltool.rml.Row;
import nl.knaw.huc.rmltool.rml.datasource.JoinHandler;
import nl.knaw.huc.rmltool.rml.datasource.RowFactory;
import nl.knaw.huc.rmltool.util.Tuple;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.List.of;
import static nl.knaw.huc.rmltool.util.Tuple.tuple;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

public class ExcelSheetDataSource implements DataSource {


  private final Sheet sheet;
  private final RowFactory rowFactory;
  private final JoinHandler joinHandler;
  private static final List<Integer> SUPPORTED_CELL_TYPES = of(CELL_TYPE_NUMERIC, CELL_TYPE_STRING, CELL_TYPE_BOOLEAN);

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
                              .filter(headerIndex -> {
                                Cell cell = row.getCell(headerIndex.getRight());
                                    return cell != null &&
                                        SUPPORTED_CELL_TYPES.contains(cell.getCellType()) &&
                                        !getCellValue(cell).equals("");
                                  }
                              )
                              .map(headerIndex -> tuple(
                                  headerIndex.getLeft(),
                                  getCellValue(row.getCell(headerIndex.getRight()))
                              )).collect(Collectors.toMap(Tuple::getLeft, Tuple::getRight));


                          return rowFactory.makeRow(values, errorHandler);
                        });
  }

  private String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case CELL_TYPE_NUMERIC : return "" + cell.getNumericCellValue();
      case CELL_TYPE_STRING : return cell.getStringCellValue();
      case CELL_TYPE_BOOLEAN : return "" + cell.getBooleanCellValue();
      default: return null;
    }
  }

  private List<String> getHeaders(Iterator<org.apache.poi.ss.usermodel.Row> iterator) {
    final org.apache.poi.ss.usermodel.Row headerRow = iterator.next();
    final List<String> headers = Lists.newArrayList();
    headerRow.cellIterator().forEachRemaining(cell -> headers.add(getCellValue(cell)));
    return headers;
  }

  @Override
  public void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName) {
    joinHandler.willBeJoinedOn(fieldName, referenceJoinValue, uri, outputFieldName);
  }
}
