package com.ai.baas.amc.topology.core.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ai.baas.storm.exception.BusinessException;
import com.ai.opt.sdk.components.base.ComponentConfigLoader;
import com.ai.opt.sdk.components.mo.PaasConf;
import com.ai.opt.sdk.components.util.ConfigTool;
import com.ai.opt.sdk.exception.SDKException;
import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;

/**
 * MDS代理类
 * Date: 2016年5月10日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class MDSProxy {
    
    public static IMessageSender getMessageSender(String authUrl,String pid,String pwd,String serviceId,String topic){
        if(StringUtils.isBlank(authUrl)||StringUtils.isBlank(pid)||StringUtils.isBlank(pwd)||StringUtils.isBlank(serviceId)||StringUtils.isBlank(topic)){
           return null;
        }
        AuthDescriptor authDescriptor = new AuthDescriptor(authUrl,
                pid, pwd, serviceId);
//        AuthDescriptor authDescriptor = new AuthDescriptor();
//        authDescriptor.setAuthAdress("http://10.1.245.4:19811/service-portal-uac-web/service/auth");
//        authDescriptor.setPassword("123456");
//        authDescriptor.setPid("87EA5A771D9647F1B5EBB600812E3067");
//        authDescriptor.setServiceId("MDS010");
        return MsgSenderFactory.getClient(authDescriptor, topic);
    }
    
}
