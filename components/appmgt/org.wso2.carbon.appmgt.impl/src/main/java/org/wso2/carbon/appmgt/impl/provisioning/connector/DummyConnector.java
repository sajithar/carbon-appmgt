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

package org.wso2.carbon.appmgt.impl.provisioning.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.impl.provisioning.ProvisioningException;
import org.wso2.carbon.appmgt.impl.provisioning.dto.Property;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisionedIdentifier;
import org.wso2.carbon.appmgt.impl.provisioning.dto.ProvisioningEntity;

/**
 * This is a dummy connector class.
 */
public class DummyConnector extends AbstractOutboundProvisioningConnector {

    private static final Log log = LogFactory.getLog(DummyConnector.class);

    @Override
    public void init(Property[] provisioningProperties) throws ProvisioningException {
        log.debug("initializing the dummy connector");
    }

    @Override
    public ProvisionedIdentifier provision(ProvisioningEntity provisioningEntity)
            throws ProvisioningException {
        log.debug("provisioning using the dummy connector");
        return null;
    }
}
