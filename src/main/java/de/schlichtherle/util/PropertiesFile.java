package de.schlichtherle.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFile {
    private  String FilePath ;
    private Properties props = new Properties();

   public PropertiesFile(String filePath){
       this.FilePath = filePath;
   }
    /**
     * 判断文件是否存在
     *
     * @return
     */
    private boolean FileExit(String FilePath, String FileName) {
        File file = new File(FilePath, FileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public Properties Loading() {
        try {
            props.load(new FileInputStream(FilePath));
        } catch (FileNotFoundException e) {
            File file = new File(FilePath);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            System.err.println("can not found "+FilePath);
        } catch (IOException e) {
            System.exit(-1);
        }
        return props;
    }

}