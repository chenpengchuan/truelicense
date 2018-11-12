package de.schlichtherle.app.rest;

import de.schlichtherle.app.databases.LicenseQuery;
import de.schlichtherle.app.databases.LicenseSave;
import de.schlichtherle.app.entity.LicenseEntity;
import de.schlichtherle.app.service.LicenseCommonUtil;
import de.schlichtherle.client.CreateLicense;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;
import de.schlichtherle.util.LicFileutils;
import org.jboss.logging.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/license")
@Controller
@RestController
@CrossOrigin
public class CreateLicenseResource {

    private static Logger logger = LoggerFactory.getLogger(CreateLicenseResource.class);

    @Autowired
    private LicenseCommonUtil commonUtil;

    @Autowired
    private LicenseQuery licenseQuery;

    @Autowired
    private LicenseSave licenseSave;

    @PostMapping("/create")
    @CrossOrigin
    public ResponseEntity createLicense(@Param LicenseEntity condition) {
        commonUtil.checkCondition(condition);
        LicenseCommonContent licenseCommonContent = commonUtil.buildLicenseCommonContent(condition);
        LicenseCommonParam licenseCommonParam = commonUtil.buildLicenseCommonParam(condition.getPassword());

        Boolean succ = new CreateLicense().create(licenseCommonParam, licenseCommonContent);
        if (succ) {
            try {
                File file = new File(commonUtil.licPath);
                FileInputStream inputs = new FileInputStream(file);
                byte[] arr = new byte[(int) file.length()];
                inputs.read(arr);
                inputs.close();
                condition.setLicense(LicFileutils.bytesToHex(arr));
                logger.info("create license Successed!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(condition, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            logger.error("create license Failed!");
            return new ResponseEntity<>("create license Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    @CrossOrigin
    public ResponseEntity save(LicenseEntity entity){
        Map<String,String> map =new HashMap<>();
       int n = licenseSave.insertIntoDatabases(entity);
       if(n == 1){
           map.put("id",entity.getId());
           return new ResponseEntity<>(map, HttpStatus.OK);
       }else {
           map.put("error","save error");
           return new ResponseEntity<>("save error", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @GetMapping("/query")
    @CrossOrigin
    public ResponseEntity<List<LicenseEntity>> query(){
        List<LicenseEntity> list = licenseQuery.queryLicenseList();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    @CrossOrigin
    public ResponseEntity<LicenseEntity> queryById(@PathVariable(value="id") String id){
        LicenseEntity entity = licenseQuery.findOneById(id);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }
}
