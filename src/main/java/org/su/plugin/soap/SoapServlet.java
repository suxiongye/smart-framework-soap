package org.su.plugin.soap;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.su.framework.helper.BeanHelper;
import org.su.framework.helper.ClassHelper;
import org.su.framework.util.CollectionUtil;
import org.su.framework.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import java.util.Set;

@WebServlet(urlPatterns = SoapConstant.SERVLET_URL, loadOnStartup = 0)
public class SoapServlet extends CXFNonSpringServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapServlet.class);

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);
        //初始化CXF总线
        Bus bus = getBus();
        BusFactory.setDefaultBus(bus);
        //发布soap服务
        publishSoapService();
    }

    private void publishSoapService(){
        //遍历所有标注soap注解的类
        Set<Class<?>> soapClassSet = ClassHelper.getClassSetByAnnotation(Soap.class);
        if (CollectionUtil.isNotEmpty(soapClassSet)){
            for (Class<?> soapClass : soapClassSet){
                //获取soap地址
                String address = getAddress(soapClass);
                //获取soap类的接口
                Class<?> soapInterfaceClass = getSoapInterfaceClass(soapClass);
                //获取soap实例
                Object soapInstance = BeanHelper.getBean(soapClass);
                //发布soap服务
                SoapHelper.publishService(address, soapInterfaceClass, soapInstance);
            }
        }
    }

    private Class<?> getSoapInterfaceClass(Class<?> soapClass){
        //获取soap实现类的第一个接口作为soap服务接口
        return soapClass.getInterfaces()[0];
    }

    private String getAddress(Class<?> soapClass){
        String address;
        //若soap注解的value不为空，则获取当前值，否则获得类名
        String soapValue = soapClass.getAnnotation(Soap.class).value();
        if (StringUtil.isNotEmpty(soapValue)){
            address = soapValue;
        }else {
            address = getSoapInterfaceClass(soapClass).getSimpleName();
        }
        //确保最前面只有一个反斜杠／
        if (!address.startsWith("/")){
            address = "/" + address;
        }
        address = address.replaceAll("\\/+", "/");
        return address;
    }
}
