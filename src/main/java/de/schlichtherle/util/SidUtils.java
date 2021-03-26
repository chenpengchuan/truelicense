package de.schlichtherle.util;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 使用md5对获取到的机器信息进行加密，生成对应的sid
 */
public class SidUtils {

    public static String getSid() {
        File caFile = new File(Config.SERVICEACCOUNT_CA_PATH);
        String sid;
        if (caFile.exists()) {
            //TODO Kubernetes env.
            sid = getSidFromKubernetes();
        } else {
            //TODO not Kubernetes env.
            sid = getSidFromSystem();
        }
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(sid.getBytes());
            // 通过base64编码成明文字符
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get hardware info from current System
     * Windows cpu,mac
     * Linux ,mac
     */
    private static String getSidFromSystem() {
        String cpu = HardWareUtils.getCPUSerial();
        String mac = HardWareUtils.getMac();
        String computerInfo = cpu + "," + mac;
        return computerInfo;
    }

    /**
     * get uuid from kubernetes cluster's namespace kube-system
     */
    private static String getSidFromKubernetes() {
        try {
            ApiClient client = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();
            V1Namespace sysNamespace = api.readNamespace("kube-system", null, null, null);
            String kub_uid = sysNamespace.getMetadata().getUid();
            return kub_uid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
