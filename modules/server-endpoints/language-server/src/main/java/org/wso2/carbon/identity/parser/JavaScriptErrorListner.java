package org.wso2.carbon.identity.parser;

import org.antlr.v4.runtime.*;

import java.util.HashMap;
import java.util.List;

public class JavaScriptErrorListner extends BaseErrorListener {

    public HashMap<int[], String[]> getRecognizer() {
        return (HashMap<int[], String[]>) recognizer;
    }

    private static HashMap<int[], String[]> recognizer = new HashMap<int[], String[]>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {

        List<String>stack=((Parser)recognizer).getRuleInvocationStack();
        this.recognizer.put((new int[]{line,charPositionInLine}),new String[]{stack.get(0),msg});

    }


}
