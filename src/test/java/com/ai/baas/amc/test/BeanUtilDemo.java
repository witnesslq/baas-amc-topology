package com.ai.baas.amc.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.ai.baas.amc.topology.core.bean.AmcProductInfoBean;
import com.ai.baas.amc.topology.core.util.AmcUtil;

public class BeanUtilDemo {
    BeanUtils beanutil = new BeanUtils();
    public static void main(String[] args) {
        AmcProductInfoBean bean = new AmcProductInfoBean();
//        try {
//            String[] arr = BeanUtils.getArrayProperty(bean, "");
//            for(String val :arr){
//                System.out.println(val);
//            }
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
        
        AmcUtil.covertCacheDataToBean(bean, new String[]{"1","2","3","4","5","6","7","8"});
        
        Field[] fields = bean.getClass().getDeclaredFields(); 
        List<Field> fieldList = new ArrayList<Field>();
        for(Field val :fields){
            System.out.println(val.getName());           
        }
    }
}
