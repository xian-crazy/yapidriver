package com.crazy.autotest.yapidriver.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Bt {
    public static final String RNL = System.getProperty("line.separator");//系统换行符
    private static Map<String, Properties> proertiesMap = new HashMap<String, Properties>();
    private static Map<String, String> mapperToDbxml = new ConcurrentHashMap<String, String>();

    /**
     * <pre>
     *  e.g.     Bt.getProv("/config/application.properties", "default.env");
     * </pre>
     *
     * @param propertisFile 读取的配置文件路径
     * @param key           配置键
     * @return String值
     */
    public static String GetProv(String propertisFile, String key) {
        String value;
        value = GetProv(propertisFile).getProperty(key);
        return value;
    }

    /**
     * @param propertisFile Bt.getProv("/config/application.properties")
     * @return
     */
    public static Properties GetProv(String propertisFile) {
        synchronized (proertiesMap) {
            if (proertiesMap.get(propertisFile) == null) {
                Properties p = new Properties();
                InputStream is = Bt.class.getResourceAsStream(propertisFile);
                try {
                    p.load(new InputStreamReader(is, "UTF-8"));
                    is.close();
                    proertiesMap.put(propertisFile, p);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return proertiesMap.get(propertisFile);
    }
}
