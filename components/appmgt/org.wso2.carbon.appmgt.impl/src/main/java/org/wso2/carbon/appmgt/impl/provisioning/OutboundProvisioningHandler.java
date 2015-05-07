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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.identity.provisioning.OutboundProvisioningManager;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;

/**
 * 
 * @author sajith
 * 
 */
public class OutboundProvisioningHandler {

	private static final Log log = LogFactory.getLog(OutboundProvisioningHandler.class);

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

	public void createUsers(String connectorName, List<String> userRoles) {
		OutboundProvisioningManager opm = OutboundProvisioningManager.getInstance();

		ProvisioningEntity provisioningEntity = null;
		String serviceProviderIdentifier = null;
		String inboundClaimDialect = null;
		String tenantDomainName = null;
		boolean jitProvisioning = false;
		opm.provision(provisioningEntity, serviceProviderIdentifier, inboundClaimDialect,
		              tenantDomainName, jitProvisioning);
	}
}
