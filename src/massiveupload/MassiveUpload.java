package massiveupload;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author luishure
 */
public class MassiveUpload {
    
    static String policyId;
    static String status;
    static int phase;
    static long recordNumber;
    static String errorMessage;
    static String record;
    static String md5;
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception  {
        // TODO code application logic here
        try {
            FileInputStream file = new FileInputStream("C://Users//LUISHURE//OneDrive - Seguros Suramericana, S.A//Documentos//NetBeansProjects//MassiveUpload//datos.xlsx");
            
            XSSFWorkbook library = new XSSFWorkbook(file);
            XSSFSheet sheet = (XSSFSheet) library.getSheetAt(0);
            Iterator<Row> itr = sheet.rowIterator();
            
            while (itr.hasNext()) {
                record = "";
                Row row = itr.next();
                Iterator<Cell> itrCelda = row.cellIterator();
                int ncamp = 1;
                
                if (row.getRowNum() == 0) {
                    continue;
                }
                
                while (itrCelda.hasNext()) {
                    Cell cell = itrCelda.next();
                    
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        switch (ncamp) {
                            case 1 -> policyId = cell.getRichStringCellValue().getString();
                            case 2 -> status = cell.getRichStringCellValue().getString();
                            case 5 -> {}
                            case 65, 66 -> md5 = cell.getRichStringCellValue().getString();
                            default -> record += cell.getRichStringCellValue().getString() + ";";
                        }
                    } else {
                        switch (ncamp) {
                            case 3 -> phase = (int) cell.getNumericCellValue();
                            case 4 -> recordNumber = (long) cell.getNumericCellValue();
                            default -> record += (long) cell.getNumericCellValue() + ";";
                        }
                    }
                    ncamp++;
                }
                record = record.replaceFirst(".$", "").replace(".", "");
                if (row.getRowNum() == 1) {
                    publishMessageNotifyPhase2(policyId, md5);
                }
                publishMessageMassivePhase2(policyId, recordNumber, record, md5);
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    public static void publishMessageNotifyPhase2(String policyNum, String md5) throws Exception {
        
        String message = "{\"POLICYID\": \"" + policyNum + "\",\"STATUS\": \"EN_PROCESO\",\"PHASE\": \"2\",\"MD5\": \"" + md5 + "\",\"QUANTITY_RECORDS_TO_PHASE\": 1}";
        SingletonRabbit singletonRabbit = SingletonRabbit.getInstance();
        singletonRabbit.publishMessage("sura.seguros.carga_masiva.ex", "sura.seguros.phase_started", message);
    
    }
    
    public static void publishMessageMassivePhase2(String policyNum, long recordNum, String record, String md5) throws Exception {
        
        String message = "{\"POLICYID\": \"" + policyNum + "\",\"STATUS\": \"EN_PROCESO\",\"PHASE\": \"2\",\"RECORD_NUMBER\": \"" + recordNum + "\", \"ERROR_MESSAGE\": \"\",\"RECORD\": \""+ record +"\",\"id\": \"c42ce34b-4f3f-4bb2-8278-de62d54e0c0d\",\"MD5\": \"f85d5f5f269938e95820633431b6bc04\",\"_rid\": \"N3gLALsgWvP1MgAAAAAAAA==\"}";
        SingletonRabbit singletonRabbit = SingletonRabbit.getInstance();
        singletonRabbit.publishMessage("sura.seguros.azure.procesar.RegistrosPc.ex", "sura.seguros.azure.procesar", message);
        
    }
    
}
