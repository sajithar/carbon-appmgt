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

package org.wso2.carbon.appmgt.impl.provisioning.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author sajith
 * 
 */
public class ProvisioningEntity implements Serializable {

	private static final long serialVersionUID = 5L;
	private ProvisioningEntityType entityType;
	private ProvisioningOperation operation;
	private ProvisionedIdentifier identifier;
	private String entityName;
	private boolean jitProvisioning;
	private Map<String, String> attributes;

	public ProvisioningEntity(ProvisioningEntityType entityType, String entityName,
	                          ProvisioningOperation operation) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.operation = operation;
	}

	public ProvisioningEntity(ProvisioningEntityType entityTpe, ProvisioningOperation operation) {
		this.entityType = entityTpe;
		this.operation = operation;
	}

	/**
	 * @return
	 */
	public ProvisioningEntityType getEntityType() {
		return entityType;
	}

	/**
	 * @return
	 */
	public ProvisioningOperation getOperation() {
		return operation;
	}

	/**
	 * @return
	 */
	public ProvisionedIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 */
	public void setIdentifier(ProvisionedIdentifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return
	 */
	public String getEntityName() {
		return entityName;
	}

	public boolean isJitProvisioning() {
		return jitProvisioning;
	}

	public void setJitProvisioning(boolean jitProvisioning) {
		this.jitProvisioning = jitProvisioning;
	}
}
