package de.schlichtherle.app.service;

import de.schlichtherle.app.entity.LicenseEntity;
import de.schlichtherle.model.LicenseCheckModel;
import de.schlichtherle.model.LicenseCommonContent;
import de.schlichtherle.model.LicenseCommonParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LicenseCommonUtil {

    /************common parameters****************/
    @Value("${license.common.content.privatealias:privatekey}")
    private String PRIVATEALIAS;

    @Value("${license.common.content.storepwd:inforefiner123}")
    private String STOREPWD;

    @Value("${license.common.content.priPath:/privateKeys.store}")
    private String priPath;

    @Value("${license.common.content.subject:license}")
    private String SUBJECT;

    @Value("${license.common.content.licPath:license.lic}")
    public String licPath;

    @Value("${license.common.content.consumerType:user}")
    private String consumerType;

    /**
     * 构建CommonParam
     * @param keypwd : password for create license
     * @return
     */
    public LicenseCommonParam buildLicenseCommonParam(String keypwd) {
        LicenseCommonParam licenseCommonParam = new LicenseCommonParam();
        licenseCommonParam.setAlias(PRIVATEALIAS);
        licenseCommonParam.setKeyPwd(keypwd);
        licenseCommonParam.setResource(priPath);
        licenseCommonParam.setSubject(SUBJECT);
        licenseCommonParam.setLicPath(licPath);
        licenseCommonParam.setStorePwd(STOREPWD);

        return licenseCommonParam;
    }


    /**
     * 构建CommonContent
     * @param condition
     * @return
     */
    public LicenseCommonContent buildLicenseCommonContent(LicenseEntity condition) {

        LicenseCommonContent licenseCommonContent = new LicenseCommonContent();
        licenseCommonContent.setConsumerAmount(1);
        licenseCommonContent.setInfo(condition.getInfo());
        licenseCommonContent.setConsumerType(consumerType);
        licenseCommonContent.setIssuedTime(condition.getCreateTime());
        licenseCommonContent.setNotAfter(condition.getNotAfter());
        licenseCommonContent.setNotBefore(condition.getNotBefore());

        LicenseCheckModel checkModel = new LicenseCheckModel();
        checkModel.setSid(condition.getSid());
        licenseCommonContent.setLicenseCheckModel(checkModel);

        return licenseCommonContent;
    }

    /**
     * 检查前端传传回来的参数是否齐全
     * @param condition
     */
    public void checkCondition(LicenseEntity condition){
        String errMessage = null;
        if(StringUtils.isEmpty(condition.getUser())){
            errMessage = "user can not be null";
        }else if(StringUtils.isEmpty(condition.getSid())){
            errMessage = "sid can not be null";
        }else if(StringUtils.isEmpty(condition.getPassword())){
            errMessage = "password can not be null";
        }else if(condition.getNotAfter() == null){
            errMessage = "notAfter can not be null";
        }

        if(!StringUtils.isEmpty(errMessage)){
            throw new IllegalArgumentException(errMessage);
        }
        SimpleDateFormat sfm = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isEmpty(condition.getCreateTime())){
            condition.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        if(StringUtils.isEmpty(condition.getNotBefore())){
            condition.setNotBefore(sfm.format(new Date()));
        }

    }
}
