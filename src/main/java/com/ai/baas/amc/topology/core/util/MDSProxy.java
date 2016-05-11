package com.ai.baas.amc.topology.core.util;

import org.apache.commons.lang.StringUtils;

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
        return MsgSenderFactory.getClient(authDescriptor, topic);
    }
    
}
