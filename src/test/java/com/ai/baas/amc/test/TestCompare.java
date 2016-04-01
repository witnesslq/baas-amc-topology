package com.ai.baas.amc.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ai.baas.amc.topology.core.bean.AmcProductInfoBean;

public class TestCompare {
    public static void main(String[] args) {
        List<AmcProductInfoBean> list = new ArrayList<AmcProductInfoBean>();
        AmcProductInfoBean bean1 = new AmcProductInfoBean();
        bean1.setPriority("1");
        bean1.setProductId("p1");
        AmcProductInfoBean bean2 = new AmcProductInfoBean();
        bean2.setPriority("5");
        bean2.setProductId("p5");
        AmcProductInfoBean bean3 = new AmcProductInfoBean();
        bean3.setPriority("3");
        bean3.setProductId("p3");
        list.add(bean2);
        list.add(bean3);
        list.add(bean1);
        
        for(AmcProductInfoBean bean :list){
            System.out.println(bean.getProductId());
        }
        
        Collections.sort(list, new Comparator<AmcProductInfoBean>() {
            public int compare(AmcProductInfoBean arg0, AmcProductInfoBean arg1) {
                return arg0.getPriority().compareTo(arg1.getPriority());
            }
        });

        for(AmcProductInfoBean bean :list){
            System.out.println(bean.getProductId());
        }
    }
}
