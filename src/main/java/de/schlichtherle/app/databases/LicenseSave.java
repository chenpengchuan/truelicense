package de.schlichtherle.app.databases;

import de.schlichtherle.app.entity.LicenseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LicenseSave {

    @Autowired
    private LicenseRepository licenseRepository;

    public LicenseEntity insertIntoDatabases(LicenseEntity entity){
        entity.setCreateTime(new SimpleDateFormat("yyyy-MM-dd hh:mm-ss").format(new Date()));
       return licenseRepository.save(entity);
    }
}
