/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.HashMap;
import java.util.List;

/**
 * JavaScriptErrorListner.
 * #TODO Have to add proper docstring
 */
public class JavaScriptErrorListner extends BaseErrorListener {

    public HashMap<int[], String[]> getRecognizer() {
        return (HashMap<int[], String[]>) recognizer;
    }

    private static HashMap<int[], String[]> recognizer = new HashMap<int[], String[]>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {

        List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
        this.recognizer.put((new int[]{line, charPositionInLine}), new String[]{stack.get(0), msg});

    }


}
