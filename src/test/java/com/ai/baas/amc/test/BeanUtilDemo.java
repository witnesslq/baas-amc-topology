package com.ai.baas.amc.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.ai.baas.amc.topology.core.bean.AmcProductInfoBean;

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
        Field[] fields = bean.getClass().getDeclaredFields(); 
        List<Field> fieldList = new ArrayList<Field>();
        for(Field val :fields){
            if(!"serialVersionUID".equals(val.getName())){
                fieldList.add(val);
            }
        }
    }
}
