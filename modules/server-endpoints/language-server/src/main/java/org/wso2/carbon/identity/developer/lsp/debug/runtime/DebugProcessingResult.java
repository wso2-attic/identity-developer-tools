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
 * Result of the debug event being processed.
 */
public class DebugProcessingResult {

    /**
     * InstructionTypes that can be used.
     */
    public enum InstructionType {
        STOP,
        CONTINUE
    }

    private InstructionType instructionType;
    private BreakpointInfo breakpointInfo;

    public DebugProcessingResult(
            InstructionType instructionType) {

        this.instructionType = instructionType;
    }

    /**
     * Gets the breakpoint Info.
     *
     * @return the breakpoint information.
     */
    public BreakpointInfo getBreakpointInfo() {

        return breakpointInfo;
    }

    /**
     * Sets the breakpoint Info.
     *
     * @param breakpointInfo the breakpoint information.
     */
    public void setBreakpointInfo(BreakpointInfo breakpointInfo) {

        this.breakpointInfo = breakpointInfo;
    }

    /**
     * Gets the Instruction Type.
     *
     * @return the type of the instruction Eg:  STOP, CONTINUE.
     */
    public InstructionType getInstructionType() {

        return instructionType;
    }
}
