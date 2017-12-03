package org.su.plugin.soap;


import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SoapHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapHelper.class);

    private static final List<Interceptor<? extends Message>> inInterceptorList = new ArrayList<Interceptor<? extends Message>>();
    private static final List<Interceptor<? extends Message>> outInterceptorList = new ArrayList<Interceptor<? extends Message>>();

    static {
        //添加Logger Interceptor
        if (SoapConfig.isLog()) {
            LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
            inInterceptorList.add(loggingInInterceptor);
            LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
            outInterceptorList.add(loggingOutInterceptor);
        }
    }

    //发布soap服务
    public static void publishService(String wsdl, Class<?> interfaceClass, Object implementInstance) {
        ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setAddress(wsdl);
        serverFactoryBean.setServiceClass(interfaceClass);
        serverFactoryBean.setServiceBean(implementInstance);
        serverFactoryBean.setInInterceptors(inInterceptorList);
        serverFactoryBean.setOutFaultInterceptors(outInterceptorList);
        serverFactoryBean.create();
    }

    //创建soap客户端
    public static <T> T createClient(String wsdl, Class<? extends T> interfaceClass) {
        ClientProxyFactoryBean factoryBean = new ClientProxyFactoryBean();
        factoryBean.setAddress(wsdl);
        LOGGER.error("create:"+wsdl);
        factoryBean.setServiceClass(interfaceClass);
        factoryBean.setInInterceptors(inInterceptorList);
        factoryBean.setOutFaultInterceptors(outInterceptorList);
        return factoryBean.create(interfaceClass);
    }
}
