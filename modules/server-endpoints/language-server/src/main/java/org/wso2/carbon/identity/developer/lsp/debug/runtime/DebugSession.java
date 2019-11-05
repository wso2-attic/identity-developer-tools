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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.Session;

/**
 * Session maintained for the debug.
 * Contains the information about the breakpoints.
 */
public class DebugSession {

    private Session session;
    private Map<String, BreakpointInfo> breakpointInfoMap = new HashMap<>();

    public Session getSession() {

        return session;
    }

    public void setSession(Session session) {

        this.session = session;
    }

    public void setBreakpoints(String resource, int[] breakpoints) {

        BreakpointInfo breakpointInfo = breakpointInfoMap.get(resource);
        if (breakpointInfo == null) {
            breakpointInfo = new BreakpointInfo();
            breakpointInfoMap.put(resource, breakpointInfo);
        }

        breakpointInfo.setBreakpoints(breakpoints);
    }

    public BreakpointInfo getBreakpointInfo(String source) {

        BreakpointInfo result = breakpointInfoMap.get(source);
        return result;
    }

    /**
     * This is temporary method, needs to be removed once created correct breakpoint identification logic.
     * @return
     *
     */
    @Deprecated
    public BreakpointInfo[] getBreakpointInfos() {

        BreakpointInfo[] result = new BreakpointInfo[breakpointInfoMap.size()];
        int i = 0;
        for (BreakpointInfo breakpointInfo : breakpointInfoMap.values()) {
            result[i] = breakpointInfo;
            i++;
        }
        return result;
    }
}
