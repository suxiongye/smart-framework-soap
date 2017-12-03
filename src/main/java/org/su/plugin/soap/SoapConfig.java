package org.su.plugin.soap;

import org.su.framework.helper.ConfigHelper;

public class SoapConfig {
    public static boolean isLog(){
        return ConfigHelper.getBoolean(SoapConstant.LOG);
    }
}
