
package org.mule.module.s3.process;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2013-07-18T05:10:21-05:00", comments = "Build UNKNOWN_BUILDNUMBER")
public interface ProcessInterceptor<T,O >{

       T execute(ProcessCallback<T, O> callback, O under, org.mule.api.processor.MessageProcessor messageProcessor, org.mule.api.MuleEvent event) throws Exception;
    T execute(ProcessCallback<T, O> callback, O under, org.mule.api.routing.filter.Filter filter, org.mule.api.MuleMessage message) throws Exception;
}