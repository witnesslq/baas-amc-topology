package com.ai.baas.amc.topology.core.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.writeoff.bean.AmcOweInfoBean;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;

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
     * 将属性值放入bean
     * @param bean
     * @return
     * @author LiangMeng
     */
    public static Object covertCacheDataToBean(Object bean,String[] values){
        List<Field> fieldList = getBeansField(bean);
        try {
            if(fieldList.size()!=values.length){
                throw new BusinessException(AmcConstants.FailConstant.FAIL_CODE_GET_CACHE_DATA, "缓存中获取数据格式不正确");
            }
            for(int i=0;i<fieldList.size();i++){
                Field field = fieldList.get(i);
                field.setAccessible(true); //设置些属性是可以访问的  
                String value = values[i];
                String type = field.getType().toString();//得到此属性的类型  
                if(type.endsWith("String")){
                    field.set(bean, String.valueOf(value));
                }
                if(type.endsWith("Long")||type.equals("long")){
                    field.set(bean, Long.valueOf(value));
                }
            }
        } catch (Exception e) {
            LOG.error("缓存中获取数据，转换报文异常:"+e,e);
            throw new BusinessException(AmcConstants.FailConstant.FAIL_CODE_GET_CACHE_DATA, "缓存中获取数据，转换报文异常");
        } 
        return fieldList;
    }
    
    /**
     * 获取未销账月份列表
     * 
     * @param acctId
     * @return
     * @author LiangMeng
     */
    public static List<Map<String, Object>> queryWriteOffMonths(String tenantId, String acctId,
            Connection conn) {
        List<Map<String, Object>> list = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select tenant_id as tenantId,acct_id as acctId,");
        sql.append("balance,month,create_time as createTime,confirm_time as confirmTime ");
        sql.append("from amc_owe_info where acct_id = ");
        sql.append(acctId);
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        AmcOweInfoBean amcOweInfoBean = null;
        try {
            if (conn != null) {
                List<AmcOweInfoBean> result = JdbcTemplate.query(sql.toString(), conn,
                        new BeanListHandler<AmcOweInfoBean>(AmcOweInfoBean.class));
                if (result != null && result.size() > 0) {
                    amcOweInfoBean = result.get(0);
                    String month = amcOweInfoBean.getMonth();
                    if (month == null) {
                        throw new SystemException("获取最后未销账月份出错");
                    } else {
                        list = DateUtil.getPerMonth(month);
                    }
                }
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }
        return list;
    }
}
