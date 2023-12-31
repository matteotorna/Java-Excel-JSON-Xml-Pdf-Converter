package com.tool.struts;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;
import com.tool.converter.XmlWriter;
import com.tool.model.ExcelRecord;
import com.tool.util.ExcelReader;

public class FileUploadAction extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private File file;
    private String fileContentType;
    private String fileFileName;
    private String xmlContent;

    private InputStream fileInputStream;

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public String execute() throws Exception {
        if (file != null) {
            // Processa il file qui
            ExcelReader reader = new ExcelReader();
            List<ExcelRecord> records = reader.readDataFromExcel(new FileInputStream(file));

            // Trasforma gli oggetti ExcelRecord in liste di mappe
            List<Map<String, Object>> recordMaps = new ArrayList<>();
            for (ExcelRecord record : records) {
                Map<String, Object> recordMap = getObjectFieldsAsMap(record);
                recordMaps.add(recordMap);
            }

            // Elabora i record qui, ad esempio convertendoli in una stringa XML
            XmlWriter writer = new XmlWriter();
            xmlContent = writer.writeXml(recordMaps); // Ottieni la stringa XML

            if (xmlContent != null) {
                // Imposta l'input stream per il risultato "success"
                fileInputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
                return SUCCESS;
            } else {
                // Gestione dell'errore se xmlContent è nullo
                return ERROR;
            }
        } else {
            return ERROR;
        }
    }

    private Map<String, Object> getObjectFieldsAsMap(Object obj) {
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        try {
            for (Field field : fields) {
                field.setAccessible(true);
                fieldMap.put(field.getName(), field.get(obj));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fieldMap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public String getXmlContent() {
        return xmlContent;
    }
}
