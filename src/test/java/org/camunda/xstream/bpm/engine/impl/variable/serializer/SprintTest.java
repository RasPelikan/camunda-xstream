package org.camunda.xstream.bpm.engine.impl.variable.serializer;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.xstream.spring.PluginConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PluginConfiguration.class)
public class SprintTest {

    @Autowired
    private ProcessEnginePlugin xstreamPlugin;

    @Test
    public void testPlugin() {

        Assert.assertEquals(org.camunda.xstream.ProcessEnginePlugin.class, xstreamPlugin.getClass());

    }

}
