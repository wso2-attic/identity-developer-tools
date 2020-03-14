/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.developer.lsp.endpoints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.developer.lsp.ServiceReferenceHolder;

import java.lang.reflect.Field;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Endpoint configurator which binds OSGI services to the endpoint.
 */
public class OSGIBindingConfigurator extends ServerEndpointConfig.Configurator {

    private static Log log = LogFactory.getLog(OSGIBindingConfigurator.class);

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {

        Object result = super.getEndpointInstance(endpointClass);
        Field[] fields = endpointClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(OsgiService.class)) {
                Class type = field.getType();
                if (log.isDebugEnabled()) {
                    log.debug("Lookup service for Field: " + field.getName() + ", type: " + type);
                }
                Object service = PrivilegedCarbonContext.getThreadLocalCarbonContext()
                        .getOSGiService(type, null);

                if (log.isDebugEnabled()) {
                    log.debug("Assigning Service: " + service + ", for field: " + field.getName());
                }
                if (service != null) {
                    field.setAccessible(true);
                    try {
                        field.set(result, service);
                    } catch (IllegalAccessException e) {
                        log.error(
                                "Could not assign the service to the field: "
                                        + endpointClass.getName() + ":"
                                        + field.getName() + ", service :" + service,
                                e);
                    }
                }
            }

            if (field.isAnnotationPresent(Inject.class)) {
                Class type = field.getType();
                if (log.isDebugEnabled()) {
                    log.debug("Lookup service for Field: " + field.getName() + ", type: " + type);
                }
                Object service = ServiceReferenceHolder.getInstance().getService(type);

                if (log.isDebugEnabled()) {
                    log.debug("Assigning Service: " + service + ", for field: " + field.getName());
                }
                if (service != null) {
                    field.setAccessible(true);
                    try {
                        field.set(result, service);
                    } catch (IllegalAccessException e) {
                        log.error(
                                "Could not assign the service to the field: " + endpointClass.getName() + ":"
                                        + field.getName() + ", service :" + service,
                                e);
                    }
                }
            }
        }

        return (T) result;
    }


}
