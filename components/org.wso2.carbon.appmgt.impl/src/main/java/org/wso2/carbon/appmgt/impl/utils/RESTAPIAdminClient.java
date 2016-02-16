/*
 *  Copyright WSO2 Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.appmgt.impl.utils;

import org.apache.axis2.AxisFault;
import org.wso2.carbon.appmgt.api.model.APIIdentifier;
import org.wso2.carbon.appmgt.impl.dto.Environment;
import org.wso2.carbon.appmgt.impl.template.APITemplateBuilder;
import org.wso2.carbon.rest.api.stub.RestApiAdminStub;
import org.wso2.carbon.rest.api.stub.types.carbon.APIData;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

public class RESTAPIAdminClient extends AbstractAPIGatewayAdminClient {

	private RestApiAdminStub restApiAdminStub;
	private String qualifiedName;
	private String qualifiedNonVersionedWebAppName;
	private Environment environment;

	public RESTAPIAdminClient(APIIdentifier appIdentifier, Environment environment) throws AxisFault {
		this.qualifiedName = appIdentifier.getProviderName() + "--" + appIdentifier.getApiName() + ":v" +
				appIdentifier.getVersion();
		this.qualifiedNonVersionedWebAppName = appIdentifier.getProviderName() + "--" + appIdentifier.getApiName();
		restApiAdminStub = new RestApiAdminStub(null, environment.getServerURL() + "RestApiAdmin");
		setup(restApiAdminStub, environment);
		this.environment = environment;
	}

	/**
	 * Adds versioned web app configuration to the gateway
	 *
	 * @param builder
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void addVersionedWebApp(APITemplateBuilder builder, String tenantDomain) throws AxisFault {
		try {
			String appConfig = builder.getConfigStringForVersionedWebAppTemplate(environment);
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.addApiForTenant(appConfig, tenantDomain);
			} else {
				restApiAdminStub.addApiFromString(appConfig);
			}
		} catch (Exception e) {
			throw new AxisFault("Error while adding new WebApp", e);
		}
	}

	/**
	 * Returns versioned web app configuration from the gateway
	 *
	 * @param tenantDomain
	 * @return
	 * @throws AxisFault
	 */
	public APIData getVersionedWebApp(String tenantDomain) throws AxisFault {
		try {
			APIData appData;
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				appData = restApiAdminStub.getApiForTenant(qualifiedName, tenantDomain);
			} else {
				appData = restApiAdminStub.getApiByName(qualifiedName);
			}
			return appData;
		} catch (Exception e) {
			throw new AxisFault("Error while obtaining WebApp information from gateway", e);
		}
	}

	/**
	 * Updates versioned web app configuration in the gateway
	 *
	 * @param builder
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void updateVersionedWebApp(APITemplateBuilder builder, String tenantDomain) throws AxisFault {
		try {
			String appConfig = builder.getConfigStringForVersionedWebAppTemplate(environment);
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.updateApiForTenant(qualifiedName, appConfig, tenantDomain);
			} else {
				restApiAdminStub.updateApiFromString(qualifiedName, appConfig);
			}
		} catch (Exception e) {
			throw new AxisFault("Error while updating WebApp", e);
		}
	}

	/**
	 * Deletes versioned web app configuration from the gateway
	 *
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void deleteVersionedWebApp(String tenantDomain) throws AxisFault {
		try {
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.deleteApiForTenant(qualifiedName, tenantDomain);
			} else {
				restApiAdminStub.deleteApi(qualifiedName);
			}
		} catch (Exception e) {
			throw new AxisFault("Error while deleting WebApp", e);
		}
	}

	/**
	 * Adds non-versioned web app configuration to the gateway
	 *
	 * @param builder
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void addNonVersionedWebApp(APITemplateBuilder builder, String tenantDomain) throws AxisFault {
		try {
			String appConfig = builder.getConfigStringForNonVersionedWebAppTemplate();
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.addApiForTenant(appConfig, tenantDomain);
			} else {
				restApiAdminStub.addApiFromString(appConfig);
			}
		} catch (Exception e) {
			throw new AxisFault("Error publishing non-versioned web app to the gateway", e);
		}
	}

	/**
	 * Updates non-versioned web app configuration in the gateway
	 *
	 * @param builder
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void updateNonVersionedWebApp(APITemplateBuilder builder, String tenantDomain) throws AxisFault {
		try {
			String appConfig = builder.getConfigStringForNonVersionedWebAppTemplate();
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.updateApiForTenant(qualifiedNonVersionedWebAppName, appConfig,
													tenantDomain);
			} else {
				restApiAdminStub.updateApiFromString(qualifiedNonVersionedWebAppName, appConfig);
			}
		} catch (Exception e) {
			throw new AxisFault("Error while updating non-versioned web app in the gateway", e);
		}
	}

	/**
	 * Deletes non-versioned web app configuration form the gateway
	 *
	 * @param tenantDomain
	 * @throws AxisFault
	 */
	public void deleteNonVersionedWebApp(String tenantDomain) throws AxisFault {
		try {
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				restApiAdminStub.deleteApiForTenant(qualifiedNonVersionedWebAppName, tenantDomain);
			} else {
				restApiAdminStub.deleteApi(qualifiedNonVersionedWebAppName);
			}
		} catch (Exception e) {
			throw new AxisFault("Error while deleting non-versioned web app from the gateway", e);
		}
	}

	/**
	 * Returns the non-versioned web app configuration from the gateway
	 *
	 * @param tenantDomain
	 * @return
	 * @throws AxisFault
	 */
	public APIData getNonVersionedWebAppData(String tenantDomain) throws AxisFault {
		try {
			APIData appData;
			if (!MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
				appData = restApiAdminStub.getApiForTenant(qualifiedNonVersionedWebAppName, tenantDomain);
			} else {
				appData = restApiAdminStub.getApiByName(qualifiedNonVersionedWebAppName);
			}
			return appData;
		} catch (Exception e) {
			throw new AxisFault(
					"Error while obtaining non-versioned web app information from gateway", e);
		}
	}
}
