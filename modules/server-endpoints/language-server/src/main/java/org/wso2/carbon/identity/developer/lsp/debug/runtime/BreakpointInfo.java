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

package org.wso2.carbon.identity.developer.lsp.debug.runtime;

/**
 * Holder for the breakpoint.
 */
public class BreakpointInfo {

    private String resourceName;
    private int[] breakpointLocations;

    /**
     *  Gets the resourceName.
     *
     * @return
     */
    public String getResourceName() {

        return resourceName;
    }

    /**
     * Sets the resourceName.
     *
     * @param resourceName
     */
    public void setResourceName(String resourceName) {

        this.resourceName = resourceName;
    }

    /**
     * Gets the breakpoint Locations.
     *
     * @return
     */
    public int[] getBreakpointLocations() {

        return breakpointLocations;
    }

    /**
     * Sets the breakpoints.
     *
     * @param breakpoints
     */
    public void setBreakpoints(int[] breakpoints) {

        breakpointLocations = breakpoints;
    }
}
