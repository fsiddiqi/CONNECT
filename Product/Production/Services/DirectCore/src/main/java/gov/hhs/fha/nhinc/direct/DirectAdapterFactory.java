/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.direct;

import gov.hhs.fha.nhinc.event.EventLoggerFactory;
import gov.hhs.fha.nhinc.mail.ManageTaskScheduler;
import gov.hhs.fha.nhinc.proxy.ComponentProxyFactory;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.nhindirect.gateway.smtp.GatewayState;

/**
 * Direct Client Factory responsible for {@link DirectAdapter}.
 */
public class DirectAdapterFactory extends DirectAdapterEntity {

    private static final Logger LOG = Logger.getLogger(DirectAdapterFactory.class);
    private static final String BEAN_NAME_MANAGE_TASK_SCHEDULER = "manageTaskScheduler";

    /**
     * Register Handlers will invoke getInstance, thereby loading the spring
     * context and task scheduler for polling mail servers.
     */
    public void registerHandlers() {
        //initialize the HibernateUtil when the Direct Servlet is initialized.. DO NOT Remove this.
        SessionFactory session = gov.hhs.fha.nhinc.event.persistence.HibernateUtil.getSessionFactory();
        session = gov.hhs.fha.nhinc.direct.messagemonitoring.persistence.HibernateUtil.getSessionFactory();
        LOG.trace("Registering event Loggers");
        EventLoggerFactory.getInstance().registerLoggers();
        LOG.trace("Registering handlers...");
        getDirectReceiver();
    }

    /**
     * Stops the default Spring Direct TaskScheduler
     *
     */
    private void stopTaskScheduler() {
        LOG.trace("stop the Spring Task Scheduler...");
        //get the manage bean scheduler
        ManageTaskScheduler manageTaskScheduler = (ManageTaskScheduler) (new ComponentProxyFactory(CONFIG_FILE_NAME)).getInstance(BEAN_NAME_MANAGE_TASK_SCHEDULER, ManageTaskScheduler.class);
        //call the bean clean to shutdown the spring default task scheduler
        if (manageTaskScheduler != null) {
            manageTaskScheduler.clean();
        }
    }

    /**
     * Stops the Direct Agent Settings Manager (
     *
     */
    private void stopAgentSettingsManager() {
        LOG.trace("stop the Direct Agent Settings Manager...");
        //stop the agent settings if its running
        if (GatewayState.getInstance().isAgentSettingManagerRunning()) {
            GatewayState.getInstance().stopAgentSettingsManager();
        }
    }

    /**
     * Stops all the active threads
     *
     */
    public void stopAll() {
        LOG.trace("stop All ...");
        //stop the spring task sceduler
        stopTaskScheduler();
        //stop the Direct Agent Settings Manager
        stopAgentSettingsManager();
    }
}
