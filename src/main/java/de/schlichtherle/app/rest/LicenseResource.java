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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/license")
@Controller
@RestController
@CrossOrigin
public class LicenseResource {

    private static Logger logger = LoggerFactory.getLogger(LicenseResource.class);

    @Autowired
    private LicenseCommonUtil commonUtil;

    @Autowired
    private LicenseQuery licenseQuery;

    @Autowired
    private LicenseSave licenseSave;

    @PostMapping("/create")
    public ResponseEntity createLicense(@Param LicenseEntity condition) {
        commonUtil.checkCondition(condition);
        LicenseCommonContent licenseCommonContent = commonUtil.buildLicenseCommonContent(condition);
        LicenseCommonParam licenseCommonParam = commonUtil.buildLicenseCommonParam(condition);

        if (!licenseCommonParam.getStorePwd().equals(licenseCommonParam.getKeyPwd())){
            throw new IllegalArgumentException("wrong password.");
        }
        Boolean succ = new CreateLicense().create(licenseCommonParam, licenseCommonContent);
        if (succ) {
            try {
                File file = new File(commonUtil.getlicPath(condition.getUser(),condition.getNotBefore()));
                FileInputStream inputs = new FileInputStream(file);
                byte[] arr = new byte[(int) file.length()];
                inputs.read(arr);
                inputs.close();
                condition.setLicense(LicFileutils.bytesToHex(arr));
                logger.info("create license Successed!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(condition, HttpStatus.OK);
        } else {
            logger.error("create license Failed!");
            return new ResponseEntity<>("create license Failed!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity save(LicenseEntity entity) {
        Map<String, String> map = new HashMap<>();
        licenseSave.insertIntoDatabases(entity);
        if (!StringUtils.isEmpty(entity.getId())) {
            map.put("id", entity.getId());
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("error", "save error");
            return new ResponseEntity<>("save error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *g根据用户名或sid查询.由于前端已经没有维护，所以根据sid查询也只能暂放在此接口;先按用户查询，如果没查询到再按sid查询。
     * @param input 用户名或sid
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<Page<LicenseEntity>> query(@RequestParam(value="user") String input,@RequestParam(value="page") int page,@RequestParam(value="limit") int limit) {
        Sort sort = new Sort(Sort.Direction.ASC,"notAfter");
        Page<LicenseEntity> rt = licenseQuery.queryLicenseListByUser(input,page,limit,sort);
        if (rt.getTotalElements() == 0 && !StringUtils.isEmpty(input)){
            rt = licenseQuery.queryLicenseBySid(input,page,limit,sort);
        }
        return new ResponseEntity<>(rt, HttpStatus.OK);
    }

}
