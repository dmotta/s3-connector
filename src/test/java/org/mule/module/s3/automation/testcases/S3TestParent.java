/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.module.s3.automation.testcases;

import org.junit.BeforeClass;
import org.mule.api.processor.MessageProcessor;
import org.mule.tck.junit4.FunctionalTestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;



public class S3TestParent extends FunctionalTestCase {
	
	protected static final String[] SPRING_CONFIG_FILES = new String[] {"AutomationSpringBeans.xml"};
	protected static ApplicationContext context;
	protected Map<String,Object> setupData;
	protected Map<String,Object> testObjects;

	
	@Override
	protected String getConfigResources() {
		return "automation-test-flows.xml";
	}
	
    protected MessageProcessor lookupFlowConstruct(String name) {
        return (MessageProcessor) muleContext.getRegistry().lookupFlowConstruct(name);
    }
	
    @BeforeClass
    public static void beforeClass(){
    	
    	context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILES);
    	
    }

}