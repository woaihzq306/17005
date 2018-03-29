package cn.yunhu.utils;

import android.text.TextUtils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;


/**
 * Created by Administrator on 2017\10\24 0024.
 */

public class EncodeUtil {

    public static String getUidFromBase64(String base64Id) {
        String result = "";

        if (!TextUtils.isEmpty(base64Id)) {
            result = new String(android.util.Base64.decode(base64Id.getBytes(), android.util.Base64.DEFAULT));
        }

        return result;
    }

    /**
     * @param str 需要加密的文字
     * @return 加密后的文字
     * @throws Exception 加密失败
     */
    public static String get3DES(final String str) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(Constants.SECRET_KEY.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory
                .getInstance(Constants.ALGORITHM);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(Constants.IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(str.getBytes(Constants.ENCODE));
        return Base64Utils.encode(encryptData);
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode3DES(String encryptText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(Constants.SECRET_KEY.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(Constants.ALGORITHM);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(Constants.IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

        byte[] decryptData = cipher.doFinal(Base64Utils.decode(encryptText));

        return new String(decryptData, Constants.ENCODE);
    }
}
