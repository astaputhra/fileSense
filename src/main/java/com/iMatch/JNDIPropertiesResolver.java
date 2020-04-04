package com.iMatch;


import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.io.IOUtils;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JNDIPropertiesResolver extends BasicDataSourceFactory implements ObjectFactory {
    private static final Pattern _propRefPattern = Pattern.compile("\\$\\{.*?\\}");
    private static final Pattern _EncodedPattern = Pattern.compile("ENC\\((.*?)\\)");

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        if (obj instanceof Reference) {
            Reference ref = (Reference) obj;
            for(int i = 0; i < ref.size(); i++) {
                RefAddr addr = ref.get(i);
                String tag = addr.getType();
                String value = (String) addr.getContent();

                Matcher matcher = _propRefPattern.matcher(value);
                if (matcher.find()) {
                    String resolvedValue = resolve(value);
                    if(resolvedValue.startsWith("ENC(")){
                        Matcher encMatcher = _EncodedPattern.matcher(resolvedValue);
                        if(encMatcher.matches()){
                            String encryptedString = encMatcher.group(1);
                            resolvedValue = decrypt(encryptedString);
                        }
                    }
                    ref.remove(i);
                    ref.add(i, new StringRefAddr(tag, resolvedValue));
                }
            }
        }
        // Return the customized instance
        return super.getObjectInstance(obj, name, nameCtx, environment);
    }
    private String resolve(String value) {
        String propName = value.substring(2, value.length()-1);
        Properties prop = new Properties();
        InputStream input = null;

        if(prop.isEmpty()){
            try {
                input = this.getClass().getClassLoader().getResourceAsStream("hexgen.properties");
                prop.load(input);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException("Exception while getting resolving property place holder " + value);
            } finally {
                if (input != null) IOUtils.closeQuietly(input);
            }
        }
        boolean historicalReportingEnabled = false;

        if(prop.getProperty("historicalReporting.enabled") != null) {
            historicalReportingEnabled = prop.getProperty("historicalReporting.enabled").equals("true");
        }

        if(!historicalReportingEnabled){
            if(propName.equals("historicalReporting.url")){
                return prop.getProperty("url");
            }
            if(propName.equals("historicalReporting.dbusername")){
                return prop.getProperty("dbusername");
            }
            if(propName.equals("historicalReporting.password")){
                return prop.getProperty("password");
            }
        }
        return prop.getProperty(propName);
    }
    private static final String password = "anish";
    private static String decrypt(String encryptedString) {
        return encryptedString;
    }
}
