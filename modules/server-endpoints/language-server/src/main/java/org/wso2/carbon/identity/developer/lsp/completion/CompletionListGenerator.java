package org.wso2.carbon.identity.developer.lsp.completion;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;

import java.util.HashMap;

/**
 * Class to generate completion list.
 */
public class CompletionListGenerator {

    private JsFunctionRegistry jsFunctionRegistry;


    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {
        this.jsFunctionRegistry = jsFunctionRegistry;
    }

    /**
     *This function generate JSON object with required keywords for given scope. Currently used for onCompletion method.
     * @param scope scope of typed string
     * @return JSON Object with required hashmap
     */
    public JsonObject getList(String scope) {

        final String finalScope = scope.toLowerCase();
        final Keywords keywords = new Keywords();
        JsonObject mainObj = new JsonObject();

        switch (finalScope) {
            case "program":
                HashMap<String, String[]> program = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);

                }};
                mainObj.add("re", generateArray(program));
                break;
            case "sourceelement":
                HashMap<String, String[]> sourceElement = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("return", keywords.return_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(sourceElement));
                break;
            case "statement":
                HashMap<String, String[]> statement = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("return", keywords.return_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(statement));
                break;
            case "block":
                HashMap<String, String[]> block = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("continue", keywords.continue_keyword);
                    put("break", keywords.break_keyword);
                    put("return", keywords.return_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(block));
                break;
            case "statementlist":
                HashMap<String, String[]> statementList = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(statementList));
                break;
            case "variablestatement":
                HashMap<String, String[]> variableStatement = new HashMap<String, String[]>() {{
                    put("variableStatement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(variableStatement));
                break;
            case "variabledeclarationlist":
                HashMap<String, String[]> variableDeclarationList = new HashMap<String, String[]>() {{
                    put("true", keywords.true_keyword);
                    put("false", keywords.false_keyword);
                    put("null", keywords.null_keyword);
                }};
                mainObj.add("re", generateArray(variableDeclarationList));
                break;
            case "variabledeclaration":
                HashMap<String, String[]> variableDeclaration = new HashMap<String, String[]>() {{
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                }};
                mainObj.add("re", generateArray(variableDeclaration));
                break;
            case "emptystatement":
                HashMap<String, String[]> emptyStatement = new HashMap<String, String[]>() {{
                    put("emptyAStatement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(emptyStatement));
                break;
            case "expressionstatement":
                HashMap<String, String[]> expressionStatement = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(expressionStatement));
                break;
            case "ifstatement":
                HashMap<String, String[]> ifStatement = new HashMap<String, String[]>() {{
                    put("if", keywords.if_keyword);
                }};
                mainObj.add("re", generateArray(ifStatement));
                break;
            case "iterationstatement":
                HashMap<String, String[]> iterationStatement = new HashMap<String, String[]>() {{
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                }};
                mainObj.add("re", generateArray(iterationStatement));
                break;
            case "varmodifier":
                HashMap<String, String[]> varModifier = new HashMap<String, String[]>() {{
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                }};
                mainObj.add("re", generateArray(varModifier));
                break;
            case "continuestatement":
                HashMap<String, String[]> continueStatement = new HashMap<String, String[]>() {{
                    put("continue", keywords.continue_keyword);
                }};
                mainObj.add("re", generateArray(continueStatement));
                break;
            case "breakstatement":
                HashMap<String, String[]> breakStatement = new HashMap<String, String[]>() {{
                    put("break", keywords.break_keyword);
                }};
                mainObj.add("re", generateArray(breakStatement));
                break;
            case "returnstatement":
                final HashMap<String, String[]> returnStatement = new HashMap<String, String[]>() {{
                    put("return", keywords.return_keyword);
                }};
                mainObj.add("re", generateArray(returnStatement));
                break;
            case "withstatement":
                HashMap<String, String[]> withStatement = new HashMap<String, String[]>() {{
                    put("withStatement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(withStatement));
                break;
            case "switchstatement":
                HashMap<String, String[]> switchStatement = new HashMap<String, String[]>() {{
                    put("switchStatement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(switchStatement));
                break;
            case "caseblock":
                HashMap<String, String[]> caseBlock = new HashMap<String, String[]>() {{
                    put("case", keywords.case_keyword);
                }};
                mainObj.add("re", generateArray(caseBlock));
                break;
            case "caseclauses":
                HashMap<String, String[]> caseClauses = new HashMap<String, String[]>() {{
                    put("case", keywords.case_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(caseClauses));
                break;
            case "caseclause":
                HashMap<String, String[]> caseClause = new HashMap<String, String[]>() {{
                    put("caseclause", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(caseClause));
                break;
            case "defaultclause":
                HashMap<String, String[]> defaultClause = new HashMap<String, String[]>() {{
                    put("default", keywords.default_keyword);
                    put("case", keywords.case_keyword);
                }};
                mainObj.add("re", generateArray(defaultClause));
                break;
            case "labelledstatement":
                HashMap<String, String[]> labelledStatement = new HashMap<String, String[]>() {{
                    put("labelled", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(labelledStatement));
                break;
            case "throwstatement":
                HashMap<String, String[]> throwStatement = new HashMap<String, String[]>() {{
                    put("throw", keywords.throw_keyword);
                }};
                mainObj.add("re", generateArray(throwStatement));
                break;
            case "trystatement":
                HashMap<String, String[]> tryStatement = new HashMap<String, String[]>() {{
                    put("try", keywords.try_keyword);
                }};
                mainObj.add("re", generateArray(tryStatement));
                break;
            case "catchproduction":
                HashMap<String, String[]> catchProduction = new HashMap<String, String[]>() {{
                    put("catch", keywords.catch_keyword);
                }};
                mainObj.add("re", generateArray(catchProduction));
                break;
            case "finallyproduction":
                HashMap<String, String[]> finallyProduction = new HashMap<String, String[]>() {{
                    put("finally", keywords.finally_keyword);
                }};
                mainObj.add("re", generateArray(finallyProduction));
                break;
            case "debuggerstatement":
                HashMap<String, String[]> debuggerStatement = new HashMap<String, String[]>() {{
                    put("debugger", keywords.debugger_keyword);
                }};
                mainObj.add("re", generateArray(debuggerStatement));
                break;
            case "functiondeclaration":
                HashMap<String, String[]> functionDeclaration = new HashMap<String, String[]>() {{
                    put("function", new String[]{"Function", "function"});
                }};
                mainObj.add("re", generateArray(functionDeclaration));
                break;
            case "classdeclaration":
                HashMap<String, String[]> classDeclaration = new HashMap<String, String[]>() {{
                    put("class", new String[]{"Class", "class"});
                }};
                mainObj.add("re", generateArray(classDeclaration));
                break;
            case "classtail":
                HashMap<String, String[]> classTail = new HashMap<String, String[]>() {{
                    put("extends", keywords.extends_keyword);
                    put("implements", keywords.implements_keyword);
                }};
                mainObj.add("re", generateArray(classTail));
                break;
            case "classelement":
                HashMap<String, String[]> classElement = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("constructor", keywords.constructor_keyword);
                    put("class", keywords.class_keyword);
                    put("static", keywords.static_keyword);
                    put("private", keywords.private_keyword);
                    put("public", keywords.public_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);

                }};
                mainObj.add("re", generateArray(classElement));
                break;
            case "methoddefinition":
                HashMap<String, String[]> methodDefinition = new HashMap<String, String[]>() {{
                    put("methodDEfinition", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(methodDefinition));
                break;
            case "generatormethod":
                HashMap<String, String[]> generatorMethod = new HashMap<String, String[]>() {{
                    put("generatorMethod", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(generatorMethod));
                break;
            case "formalparameterlist":
                HashMap<String, String[]> formalParameterList = new HashMap<String, String[]>() {{
                    put("formalparametrListt", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(formalParameterList));
                break;
            case "formalparameterarg":
                HashMap<String, String[]> formalParameterArg = new HashMap<String, String[]>() {{
                    put("formalParamaterArg", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(formalParameterArg));
                break;
            case "lastformalparameterarg":
                HashMap<String, String[]> lastFormalParameterArg = new HashMap<String, String[]>() {{
                    put("lastFormalPArameter", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(lastFormalParameterArg));
                break;
            case "functionbody":
                HashMap<String, String[]> functionBody = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);

                }};
                if (jsFunctionRegistry != null) {
                    for (String jsFunctionFromRegistry : jsFunctionRegistry.getSubsystemFunctionsMap(
                            JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER).keySet()) {
                        functionBody.put(jsFunctionFromRegistry, new String[]{"Code", jsFunctionFromRegistry});
                    }
                }
                mainObj.add("re", generateArray(functionBody));
                break;
            case "sourceelements":
                HashMap<String, String[]> sourceElements = new HashMap<String, String[]>() {{
                    put("sourceElement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(sourceElements));
                break;
            case "arrayliteral":
                HashMap<String, String[]> arrayLiteral = new HashMap<String, String[]>() {{
                    put("arrayLiteral", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(arrayLiteral));
                break;
            case "elementlist":
                HashMap<String, String[]> elementList = new HashMap<String, String[]>() {{
                    put("elementList", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(elementList));
                break;
            case "lastelement":
                HashMap<String, String[]> lastElement = new HashMap<String, String[]>() {{
                    put("lastElement", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(lastElement));
                break;
            case "objectliteral":
                HashMap<String, String[]> objectLiteral = new HashMap<String, String[]>() {{
                    put("objectiveLiteral", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(objectLiteral));
                break;
            case "propertyassignment":
                HashMap<String, String[]> propertyAssignment = new HashMap<String, String[]>() {{
                    put("propertyAssignment", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(propertyAssignment));
                break;
            case "propertyname":
                HashMap<String, String[]> propertyName = new HashMap<String, String[]>() {{
                    put("propertyName", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(propertyName));
                break;
            case "arguments":
                HashMap<String, String[]> arguments = new HashMap<String, String[]>() {{
                    put("argument", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(arguments));
                break;
            case "lastargument":
                HashMap<String, String[]> lastArgument = new HashMap<String, String[]>() {{
                    put("lastargument", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(lastArgument));
                break;
            case "expressionsequence":
                HashMap<String, String[]> expressionSequence = new HashMap<String, String[]>() {{
                    put("function", keywords.function_keyword);
                    put("if", keywords.if_keyword);
                    put("try", keywords.try_keyword);
                    put("class", keywords.class_keyword);
                    put("for", keywords.for_keyword);
                    put("while", keywords.while_keyword);
                    put("do", keywords.do_keyword);
                    put("var", keywords.var_keyword);
                    put("let", keywords.let_keyword);
                    put("const", keywords.const_keyword);
                    put("switch", keywords.switch_keyword);
                }};
                mainObj.add("re", generateArray(expressionSequence));
                break;
            case "singleexpression":
                HashMap<String, String[]> singleExpression = new HashMap<String, String[]>() {{
                    put("singleExpression", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(singleExpression));
                break;
            case "arrowfunctionparameters":
                HashMap<String, String[]> arrowFunctionParameters = new HashMap<String, String[]>() {{
                    put("arrowFunction", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(arrowFunctionParameters));
                break;
            case "arrowfunctionbody":
                HashMap<String, String[]> arrowFunctionBody = new HashMap<String, String[]>() {{
                    put("arrowfunction", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(arrowFunctionBody));
                break;
            case "assignmentoperator":
                HashMap<String, String[]> assignmentOperator = new HashMap<String, String[]>() {{
                    put("assignmentOpertaor", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(assignmentOperator));
                break;
            case "literal":
                HashMap<String, String[]> literal = new HashMap<String, String[]>() {{
                    put("literal", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(literal));
                break;
            case "numericliteral":
                HashMap<String, String[]> numericLiteral = new HashMap<String, String[]>() {{
                    put("numericLiteral", new String[]{"c", "d"});
                }};

                mainObj.add("re", generateArray(numericLiteral));
                break;
            case "identifiername":
                HashMap<String, String[]> identifierName = new HashMap<String, String[]>() {{
                    put("identifierNAme", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(identifierName));
                break;
            case "reservedword":
                HashMap<String, String[]> reservedWord = new HashMap<String, String[]>() {{
                    put("reserved", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(reservedWord));
                break;
            case "keyword":
                HashMap<String, String[]> keyword = new HashMap<String, String[]>() {{
                    put("keyword", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(keyword));
                break;
            case "getter":
                HashMap<String, String[]> getter = new HashMap<String, String[]>() {{
                    put("getter", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(getter));
                break;
            case "setter":
                HashMap<String, String[]> setter = new HashMap<String, String[]>() {{
                    put("setter", new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(setter));
                break;
            case "eos":
                HashMap<String, String[]> eos = new HashMap<String, String[]>() {{
                    put(";", new String[]{"End of Statement", ";"});
                }};
                mainObj.add("re", generateArray(eos));
                break;
            default:
                HashMap<String, String[]> whenNull = new HashMap<String, String[]>() {{
                    put(finalScope, new String[]{"c", "d"});
                }};
                mainObj.add("re", generateArray(whenNull));
                break;
        }

        return mainObj;
    }

    private JsonArray generateArray(HashMap<String, String[]> keywords) {

        JsonArray arr = new JsonArray();
        for (String key : keywords.keySet()) {
            JsonObject json = new JsonObject();
            json.addProperty("label", key);
            json.addProperty("kind", keywords.get(key)[0]);
            json.addProperty("insertText", keywords.get(key)[1]);
            arr.add(json);
        }
        return arr;
    }

}
