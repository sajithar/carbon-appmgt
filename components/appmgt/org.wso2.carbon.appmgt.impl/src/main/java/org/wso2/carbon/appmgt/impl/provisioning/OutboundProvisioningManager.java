/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmgt.impl.provisioning;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.appmgt.impl.provisioning.connector.AbstractOutboundProvisioningConnector;
import org.wso2.carbon.appmgt.impl.provisioning.dto.Property;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisionedIdentifier;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisioningEntity;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisioningEntityType;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisioningOperation;
import org.wso2.carbon.idp.mgt.ui.client.IdentityProviderMgtServiceClient;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.user.api.UserStoreException;

/**
 * This class manager outbound provisioning.
 */
public class OutboundProvisioningManager {

	private static final Log log = LogFactory.getLog(OutboundProvisioningManager.class);

	private AbstractOutboundProvisioningConnector loadConnector(String connectorName)
	                                                                                 throws ProvisioningException {

		int index = getAllRegisteredConnectors().indexOf(connectorName);
		if (index == -1) {
			String msg = "Connector '" + connectorName + "' is not configured";
			log.error(msg);
			throw new ProvisioningException(msg);
		}
		String connectorClassName = getAllRegisteredConnectorClasses().get(index);

		Class<?> connectorClass;
		try {
			connectorClass = Class.forName(connectorClassName);
		} catch (LinkageError e) {
			/*
			 * even though catching errors is not recommended, here LinkageError
			 * is handled to avoid crashing APPM when loading 3rd party classes
			 * see also: http://stackoverflow.com/a/352842/1577286
			 */
			String msg = "Linkage failed when loading connector class " + connectorClassName;
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		} catch (ClassNotFoundException e) {
			String msg = "Connector class '" + connectorClassName + "' cannot be located";
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		}

		if (!AbstractOutboundProvisioningConnector.class.isAssignableFrom(connectorClass)) {
			String msg =
			             "Connector class '" + connectorClassName +
			                     "' is not a valid outbound provisioning connector";
			log.error(msg);
			throw new ProvisioningException(msg);
		}

		Constructor<?> connectorConstructor;
		try {
			connectorConstructor = connectorClass.getConstructor();
		} catch (NoSuchMethodException e) {
			String msg = "No constructor not found for connector class " + connectorClassName;
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		} catch (SecurityException e) {
			String msg =
			             "Security exception when calling default constructor of connector class " +
			                     connectorClassName;
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		}

		Object connector;
		try {
			connector = connectorConstructor.newInstance();
		} catch (InstantiationException e) {
			String msg =
			             "Connector class '" + connectorClassName +
			                     "' either abstract or an interface";
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		} catch (IllegalAccessException e) {
			String msg = "Default constructor not found for connector class " + connectorClassName;
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		} catch (InvocationTargetException e) {
			String msg =
			             "Default constructor of connector class '" + connectorClassName +
			                     "' throws an exception";
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		}

		return (AbstractOutboundProvisioningConnector) connector;
	}

	private List<String> getUsernamesOfRole(String role) throws ProvisioningException {
		try {
			String[] tmp =
			               ServiceReferenceHolder.getInstance()
			                                     .getRealmService()
			                                     .getTenantUserRealm(MultitenantConstants.SUPER_TENANT_ID)
			                                     .getUserStoreManager().getUserListOfRole(role);
			return Arrays.asList(tmp);
		} catch (UserStoreException e) {
			String msg = "Cannot retrieve usernames of role " + role;
			log.error(msg, e);
			throw new ProvisioningException(msg, e);
		}
	}

	public List<String> getAllRegisteredConnectors() {
		return ServiceReferenceHolder.getInstance().getAPIManagerConfigurationService()
		                             .getAPIManagerConfiguration()
		                             .getProperty("NoProxyApps.ProvisioningConnector.name");
	}

	public List<String> getAllRegisteredConnectorClasses() {
		return ServiceReferenceHolder.getInstance().getAPIManagerConfigurationService()
		                             .getAPIManagerConfiguration()
		                             .getProperty("NoProxyApps.ProvisioningConnector.class");
	}

	public List<ProvisionedIdentifier> createUsers(String connectorName, List<String> userRoles)
	                                                                                      throws ProvisioningException {
		ProvisioningEntity provisioningEntity =
		                                        new ProvisioningEntity(ProvisioningEntityType.USER,
		                                                               ProvisioningOperation.CREATE);
		AbstractOutboundProvisioningConnector connector = loadConnector(connectorName);
		
		try {
	        String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
	        String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
	        ConfigurationContext configContext =
	                (ConfigurationContext) config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
	        IdentityProviderMgtServiceClient client = new IdentityProviderMgtServiceClient(cookie, backendServerURL, configContext);
	        String[] tenantIdPs = client.getTenantIdPs();
	        if(tenantIdPs == null){
	            tenantIdPs = new String[0];
	        }
	        session.setAttribute("tenantIdPList", tenantIdPs);
	    } catch (Exception e) {
	        String message = MessageFormat.format(resourceBundle.getString("error.loading.idps"),
	                new Object[]{e.getMessage()});
	        CarbonUIMessage.sendCarbonUIMessage(message, CarbonUIMessage.ERROR, request);
	    }
		
		return null;
	}

	public ProvisionedIdentifier provision(String connectorName, Property[] provisioningProperties,
	                                       ProvisioningEntity provisioningEntity)
	                                                                             throws ProvisioningException {
		AbstractOutboundProvisioningConnector connector = loadConnector(connectorName);
		connector.init(provisioningProperties);
		return connector.provision(provisioningEntity);
	}

}
