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

package org.wso2.carbon.identity.developer.lsp.debug.dap.messages;

import java.util.List;

/**
 * Breakpoint request.
 */
public class BreakpointRequest extends Request {

    private String sourceName;
    private String sourcePath;
    private int sourceReference;
    private Object adapterData;
    private int[] lines;
    private int[] breakpoints;
    private boolean sourceModified;

    public BreakpointRequest(long seq, String type, String command,
                             List<Argument> arguments) {

        super(seq, type, command, arguments);
    }

    public String getSourceName() {

        return sourceName;
    }

    public void setSourceName(String sourceName) {

        this.sourceName = sourceName;
    }

    public String getSourcePath() {

        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {

        this.sourcePath = sourcePath;
    }

    public int getSourceReference() {

        return sourceReference;
    }

    public void setSourceReference(int sourceReference) {

        this.sourceReference = sourceReference;
    }

    public Object getAdapterData() {

        return adapterData;
    }

    public void setAdapterData(Object adapterData) {

        this.adapterData = adapterData;
    }

    public int[] getLines() {

        return lines;
    }

    public void setLines(int[] lines) {

        this.lines = lines;
    }

    public int[] getBreakpoints() {

        return breakpoints;
    }

    public void setBreakpoints(int[] breakpoints) {

        this.breakpoints = breakpoints;
    }

    public boolean isSourceModified() {

        return sourceModified;
    }

    public void setSourceModified(boolean sourceModified) {

        this.sourceModified = sourceModified;
    }
}
