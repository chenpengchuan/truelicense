package de.schlichtherle.app.rest;
/*
import de.schlichtherle.app.databases.LicenseRepository;
import de.schlichtherle.app.entity.LicenseEntity;
import de.schlichtherle.util.FileBean;
import de.schlichtherle.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RequestMapping("/api/import")
@Controller
@RestController
public class DataImportResource {
    @Autowired
    LicenseRepository licenseRepository;

    private static String FilePath = "C:/Users/Administrator/Desktop/keys";

    @GetMapping
    public void importFile() {
        List<LicenseEntity> lics = new ArrayList<>();

        FileUtils p = new FileUtils();
        List<FileBean> list = new ArrayList<>();
        p.listREADME(FilePath,list);
        for(FileBean fb:list){
            LicenseEntity entity = new LicenseEntity();
            Properties props =new Properties();
            p.Loading(props,fb.getReadme());
            entity.setUser(p.getValueByekey(props,"COMPANY").trim());
            entity.setInfo(p.getValueByekey(props,"info").trim());
            entity.setSid(p.getValueByekey(props,"sid").trim());
            entity.setCreateTime(p.getValueByekey(props,"createTime").trim());
            entity.setNotBefore(p.getValueByekey(props,"notBefore").trim());
            entity.setNotAfter(p.getValueByekey(props,"notAfter").trim());

            String license = p.readLicenseKey(fb.getKey());
            entity.setLicense(license.trim());

            lics.add(entity);

        }
     licenseRepository.saveAll(lics);
    }
}
*/