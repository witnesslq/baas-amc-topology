package com.ai.baas.amc.topology.core.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.BusinessException;

public class AmcUtil {

    private static Logger LOG = LoggerFactory.getLogger(AmcUtil.class);
    
    /**
     * 执行jdbc更新操作
     * @param sql
     * @return
     * @author LiangMeng
     */
    public static  int excuteSql(String sql) {
        int result = 0;
        Connection conn = null;
        try {
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            result = JdbcTemplate.update(sql, conn);
        } catch (Exception e) {
            LOG.error("JDBC执行异常：["+e.getMessage()+"]",e);
        }
       
        return result;
    }
    
    /**
     * 获取bean的属性列表
     * @param bean
     * @return
     * @author LiangMeng
     */
    public static List<Field> getBeansField(Object bean){
        Field[] fields = bean.getClass().getDeclaredFields(); 
        List<Field> fieldList = new ArrayList<Field>();
        for(Field val :fields){
            if(!"serialVersionUID".equals(val.getName())){
                fieldList.add(val);
            }
        }
        return fieldList;
    }
    
    /**
     * 获取bean的属性列表
     * @param bean
     * @return
     * @author LiangMeng
     */
    public static Object covertCacheDataToBean(Object bean,String[] values){
        List<Field> fieldList = getBeansField(bean);
        if(fieldList.size()!=values.length){
            throw new BusinessException(AmcConstants.FailConstant.FAIL_CODE_GET_CACHE_DATA, "缓存中获取数据格式不正确");
        }
        for(Field field :fieldList){
            String type = field.getType().toString();//得到此属性的类型  
            if("String".equals(type)){
//                field.set(obj, value);
            }
        }
        return fieldList;
    }
}
