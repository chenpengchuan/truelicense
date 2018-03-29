package de.schlichtherle.model;

import java.util.List;

/**
 * 扩展额外需校验参数
 */
public class LicenseCheckModel {

    /**
     * 授权mac list集合
     */
    private String sid;
    private List<String> macAddressList;


    public LicenseCheckModel() {

    }

    public List<String> getMacAddressList() {
        return macAddressList;
    }

    public void setMacAddressList(List<String> macAddressList) {
        this.macAddressList = macAddressList;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}