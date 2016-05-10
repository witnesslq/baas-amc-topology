package com.ai.baas.amc.test;

import org.junit.Test;

import com.ai.baas.amc.topology.core.util.MDSProxy;
import com.ai.paas.ipaas.mds.IMessageSender;

public class TestMds {

    @Test
    public void testMds(){
        IMessageSender messageSender = MDSProxy.getMessageSender(
                "http://10.1.245.4:19811/service-portal-uac-web/service/auth",
                "87EA5A771D9647F1B5EBB600812E3067",
                "123456", 
                "MDS002", 
                "ECBCA29571714183B23A630E2311DD66_MDS002_1529549922");
        messageSender.send("TEST", 1);
    }
}
