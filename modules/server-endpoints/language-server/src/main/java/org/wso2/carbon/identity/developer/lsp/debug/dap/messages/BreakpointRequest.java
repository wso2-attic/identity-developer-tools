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
 * This class will help to add Breakpoint request from the Extension.
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

        super(type, seq, command, arguments);
    }

    /**
     * Gets the sourceName.
     *
     * @return the name of the Source.
     */
    public String getSourceName() {

        return sourceName;
    }

    /**
     * Sets the sourceName.
     *
     * @param sourceName the name of the Source.
     */
    public void setSourceName(String sourceName) {

        this.sourceName = sourceName;
    }

    /**
     * Gets the sourcePath.
     *
     * @return the path of the source.
     */
    public String getSourcePath() {

        return sourcePath;
    }

    /**
     * Sets the sourcePath.
     *
     * @param sourcePath the path of the source.
     */
    public void setSourcePath(String sourcePath) {

        this.sourcePath = sourcePath;
    }

    /**
     * Gets the sourceReference.
     *
     * @return the reference to the source.
     */
    public int getSourceReference() {

        return sourceReference;
    }

    /**
     * Sets the sourceReference.
     *
     * @param sourceReference The reference to the source. This is the same as source.sourceReference. This is
     *                        provided for backward compatibility since old backends do not understand the 'source'
     *                        attribute.
     */
    public void setSourceReference(int sourceReference) {

        this.sourceReference = sourceReference;
    }

    /**
     * Gets the adapterData.
     *
     * @return optional data that a debug adapter might want to loop through the client.
     */
    public Object getAdapterData() {

        return adapterData;
    }

    /**
     * Sets the adapterData.
     *
     * @param adapterData Optional data that a debug adapter might want to loop through the client. The client should
     *                    leave the data intact and persist it across sessions. The client should not interpret the data.
     */
    public void setAdapterData(Object adapterData) {

        this.adapterData = adapterData;
    }

    /**
     * Gets the lines.
     *
     * @return the code locations of the breakpoints.
     */
    public int[] getLines() {

        return lines;
    }

    /**
     * Sets the lines.
     *
     * @param lines the code locations of the breakpoints.
     */
    public void setLines(int[] lines) {

        this.lines = lines;
    }

    /**
     * Gets the breakpoints.
     *
     * @return the information about the breakpoints.
     */
    public int[] getBreakpoints() {

        return breakpoints;
    }

    /**
     * Sets the breakpoints.
     *
     * @param breakpoints information about the breakpoints.
     */
    public void setBreakpoints(int[] breakpoints) {

        this.breakpoints = breakpoints;
    }

    /**
     * Gets whether sourceModified .
     *
     * @return whether sourceModified.
     */
    public boolean isSourceModified() {

        return sourceModified;
    }

    /**
     * Sets whether sourceModified.
     *
     * @param sourceModified A value of true indicates that the underlying source has been modified which results in
     *                       new breakpoint locations.
     */
    public void setSourceModified(boolean sourceModified) {

        this.sourceModified = sourceModified;
    }
}
