package com.spdcl.email.utils;

import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import com.spdcl.utils.ServiceException;


@Service("emailUtils")
public class EmailUtils {
	private final static String LOG_VELOCITY_CLASS = 
		        "org.apache.velocity.runtime.log.NullLogSystem";
    private final static String ENCODING = "UTF-8";
	/**
     * <p>initVelocity: used to initialize the Veloctiy engine</p>
     * @throws java.lang.Exception
     * @throws com.ibm.csa.fixedprice.mail.exceptions.MailException
     */
    public static void initVelocity() throws ServiceException {
        try {
            // Encoding
            Velocity.setProperty(Velocity.INPUT_ENCODING, ENCODING);
            Velocity.setProperty(Velocity.OUTPUT_ENCODING, ENCODING);
            // Log
            Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, 
                    LOG_VELOCITY_CLASS);
//            // Resource Path                
//            Velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, 
//                   "");
            Velocity.init();
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
    } 
}
