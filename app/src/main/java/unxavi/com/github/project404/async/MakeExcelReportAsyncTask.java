package unxavi.com.github.project404.async;


import android.os.AsyncTask;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import unxavi.com.github.project404.model.WorkLog;
import unxavi.com.github.project404.utils.Utils;

public class MakeExcelReportAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String WORK_LOG_REPORT_SHEET = "Work Log Report";
    private static final String DATE = "Date";
    private static final String TASK = "Task";
    private static final String ACTION = "Action";
    private static final String COORDINATES = "Coordinates";
    private static final String WORKLOG_REPORT_XLS_FILENAME = "worklog_report.xls";

    public interface MakeExcelListener {
        void onExcelDoneListener(String filePath);
    }

    private final WeakReference<MakeExcelListener> weakListener;

    private final Locale locale;

    private final File externalFilesDir;

    private final List<WorkLog> worklogs;

    public MakeExcelReportAsyncTask(MakeExcelListener listener, Locale locale, File externalFilesDir, List<WorkLog> workLogs) {
        this.weakListener = new WeakReference<>(listener);
        this.locale = locale;
        this.externalFilesDir = externalFilesDir;
        this.worklogs = workLogs;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String excelFilePath = null;
        excelFilePath = makeExcelFile();
        return excelFilePath;
    }

    private String makeExcelFile() {
        String filePath = null;
        //New Workbook
        Workbook wb = new HSSFWorkbook();
        Cell c;
        //style for header row
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font fontHeader = wb.createFont();
        fontHeader.setColor(HSSFColor.WHITE.index);
        fontHeader.setBold(true);
        headerStyle.setFont(fontHeader);
        //day cell style
        CellStyle dataCellStyle = wb.createCellStyle();
        dataCellStyle.setAlignment(HorizontalAlignment.CENTER);
        //New Sheet
        Sheet sheet;
        sheet = wb.createSheet(WORK_LOG_REPORT_SHEET);
        //set width of columns
        sheet.setColumnWidth(0, (15 * 500));
        sheet.setColumnWidth(1, (15 * 500));
        sheet.setColumnWidth(2, (15 * 500));
        sheet.setColumnWidth(3, (15 * 500));
        //header data
        Row row = sheet.createRow(0);

        c = row.createCell(0);
        c.setCellValue(DATE);
        c.setCellStyle(headerStyle);

        c = row.createCell(1);
        c.setCellValue(TASK);
        c.setCellStyle(headerStyle);

        c = row.createCell(2);
        c.setCellValue(ACTION);
        c.setCellStyle(headerStyle);

        c = row.createCell(3);
        c.setCellValue(COORDINATES);
        c.setCellStyle(headerStyle);

        if (!worklogs.isEmpty()) {
            for (int i = 0; i < worklogs.size(); i++) {
                int actualRow = i + 1;
                WorkLog workLog = worklogs.get(i);
                if (workLog != null) {
                    Row rowAction = sheet.createRow(actualRow);
                    Cell rowCell = rowAction.createCell(0);
                    rowCell.setCellStyle(dataCellStyle);
                    rowCell.setCellValue(Utils.dateToString(workLog.getTimestamp(), locale));

                    rowCell = rowAction.createCell(1);
                    rowCell.setCellStyle(dataCellStyle);
                    if (workLog.getTask() != null && workLog.getTask().getName() != null) {
                        rowCell.setCellValue(workLog.getTask().getName());
                    }

                    rowCell = rowAction.createCell(2);
                    rowCell.setCellStyle(dataCellStyle);
                    rowCell.setCellValue(workLog.getActionString());

                    rowCell = rowAction.createCell(3);
                    rowCell.setCellStyle(dataCellStyle);
                    rowCell.setCellValue(String.format("%s,%s", workLog.getLatitude(), workLog.getLongitude()));
                }
            }
        }
        // Create a path where we will place our List of objects on external storage
        File file = new File(externalFilesDir, WORKLOG_REPORT_XLS_FILENAME);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            filePath = file.getPath();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            try {
                if (os != null)
                    os.close();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return filePath;
    }


    @Override
    protected void onPostExecute(String filePath) {
        MakeExcelListener listener = weakListener.get();
        if (listener != null) {
            listener.onExcelDoneListener(filePath);
        }
    }

}
