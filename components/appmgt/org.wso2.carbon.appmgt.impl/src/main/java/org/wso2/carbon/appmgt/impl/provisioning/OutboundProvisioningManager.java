/*
 *  Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.appmgt.impl.provisioning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.provisioning.connector.AbstractOutboundProvisioningConnector;
import org.wso2.carbon.appmgt.impl.provisioning.dto.Property;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisionedIdentifier;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisioningEntity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * This class manager outbound provisioning.
 */
public class OutboundProvisioningManager {

    private static final Log log = LogFactory.getLog(OutboundProvisioningManager.class);


    private AbstractOutboundProvisioningConnector loadConnector(String connectorName)
            throws ProvisioningException {

        int index = getAllRegisteredConnectors().indexOf(connectorName);
        if (index == -1) {
            log.error("Connector '" + connectorName + "' is not configured");
            throw new ProvisioningException("Connector '" + connectorName + "' is not configured");
        }
        String connectorClassName = getAllRegisteredConnectorClasses().get(index);

        Class connectorClass;
        try {
            connectorClass = Class.forName(connectorClassName);
        } catch (ExceptionInInitializerError e) {
            log.error("Initialization failed when loading connector class " + connectorClassName, e);
            throw new ProvisioningException("Initialization failed when loading connector class "
                                            + connectorClassName, e);
        } catch (LinkageError e) {
            log.error("Linkage failed when loading connector class " + connectorClassName, e);
            throw new ProvisioningException("Linkage failed when loading connector class "
                                            + connectorClassName, e);
        } catch (ClassNotFoundException e) {
            log.error("Connector class '" + connectorClassName + "' cannot be located", e);
            throw new ProvisioningException("Connector class '" + connectorClassName
                                            + "' cannot be located", e);
        }

        if (!connectorClass.isAssignableFrom(AbstractOutboundProvisioningConnector.class)) {
            log.error("Connector class '" + connectorClassName
                      + "' is not a valid outbound provisioning connector");
            throw new ProvisioningException("Connector class '" + connectorClassName
                                            + "' is not a valid outbound provisioning connector");
        }

        Constructor connectorConstructor;
        try {
            connectorConstructor = connectorClass.getConstructor();
        } catch (NoSuchMethodException e) {
            log.error("No constructor not found for connector class " + connectorClassName, e);
            throw new ProvisioningException("No constructor not found for connector class "
                                            + connectorClassName, e);
        } catch (SecurityException e) {
            log.error("Security exception when calling default constructor of connector class "
                      + connectorClassName, e);
            throw new ProvisioningException("Security exception when calling default constructor" +
                                            " of connector class " + connectorClassName, e);
        }

        Object connector;
        try {
            connector = connectorConstructor.newInstance();
        } catch (InstantiationException e) {
            log.error("Connector class '" + connectorClassName
                      + "' either abstract or an interface", e);
            throw new ProvisioningException("Connector class '" + connectorClassName
                                            + "' either abstract or an interface", e);
        } catch (IllegalAccessException e) {
            log.error("Default constructor not found for connector class " + connectorClassName, e);
            throw new ProvisioningException("Default constructor not found for connector class "
                                            + connectorClassName, e);
        } catch (InvocationTargetException e) {
            log.error("Default constructor of connector class '" + connectorClassName
                      + "' throws an exception", e);
            throw new ProvisioningException("Default constructor of connector class '"
                                            + connectorClassName + "' throws an exception", e);
        }

        return (AbstractOutboundProvisioningConnector) connector;

    }

    public List<String> getAllRegisteredConnectors() {
        List<String> connectorsNames = ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().
                getAPIManagerConfiguration().getProperty("NoProxyApps.ProvisioningConnector.name");
        return connectorsNames;
    }

    public List<String> getAllRegisteredConnectorClasses() {
        List<String> connectorsClasses = ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().
                getAPIManagerConfiguration().getProperty("NoProxyApps.ProvisioningConnector.class");
        return connectorsClasses;
    }

    public ProvisionedIdentifier provision(Property[] provisioningProperties,
                                           ProvisioningEntity provisioningEntity,
                                           String connectorName) throws
                                                                 ProvisioningException {
        AbstractOutboundProvisioningConnector connector = loadConnector(connectorName);
        connector.init(provisioningProperties);
        return connector.provision(provisioningEntity);
    }
}
