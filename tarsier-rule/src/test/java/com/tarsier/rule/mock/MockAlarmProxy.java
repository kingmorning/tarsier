/**
 * 
 */
package com.tarsier.rule.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.rule.data.AlarmInfo;
import com.tarsier.rule.proxy.AlarmSystemProxy;

/**
 * <p>注释</p>
 * @author Your name
 * @version $Id: MockAlarmProxy.java, v 0.1 2016年8月23日 下午6:27:24 Administrator Exp $
 */
public class MockAlarmProxy implements AlarmSystemProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockAlarmProxy.class);
    /* (non-Javadoc)
     * @see com.tarsier.rule.proxy.AlarmSystemProxy#alarm(com.tarsier.rule.data.AlarmInfo)
     */
    @Override
    public boolean alarm(AlarmInfo alarmInfo) {
        LOGGER.info(alarmInfo.toString());
        return true;
    }
	@Override
	public void stop() {
		// 
		
	}
	@Override
	public void start() {
		// 
		
	}

}
