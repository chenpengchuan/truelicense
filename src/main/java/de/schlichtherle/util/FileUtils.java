package de.schlichtherle.util;

import java.io.*;
import java.util.*;

public class FileUtils {
    private static String FilePath = "C:/Users/Administrator/Desktop/keys";
//    private Properties props = new Properties();

//    public static void main(String[] args) throws IOException {
//        FileUtils p = new FileUtils();
//        List<FileBean> list = new ArrayList<>();
//         p.listREADME(FilePath,list);
//         for(FileBean fb:list){
//             String license = p.readLicenseKey(fb.getKey());
//             Properties props =new Properties();
//             p.Loading(props,fb.getReadme());
//             String user = p.getValueByekey(props,"COMPANY");
//             System.out.println(user);
//         }
//
//    }

    public void listREADME(String path, List<FileBean> res){
        File file = new File(path);
         if (file.isDirectory()){
            String[] list = file.list();
            for(int i=0;i<list.length;i++){
                listREADME(path+"/"+list[i],res);
            }
        }else if(file.isFile()&&path.contains("README.txt")){
             FileBean bean=new FileBean();
             bean.setReadme(path);
             bean.setKey(path.substring(0,path.length()-"README.txt".length())+"license.key");
             res.add(bean);
         }
    }


    public String readLicenseKey(String filePath) {
        String str = "";
        try {
            // String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断是否是文件
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    str += lineTxt;
                }
                // System.out.println(str);
                bufferedReader.close();
                read.close();
            } else {
                System.out.println(filePath + "不是文件");
            }
        } catch (Exception e) {
            System.out.println("读文件出错");
            e.printStackTrace();
        }
        return str;
    }


    public void Loading(Properties props, String FilePath) {
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
            System.out.println("文件不存在！");
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    public String getValueByekey(Properties props, String key) {
        String value = null;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

}
