package org.wso2.carbon.identity.developer.lsp.completion;

public class Keywords {
    String[] function_keyword = new String[]{"Function", "function ${1:functionName} (){\n\t\n}"};

    String[] if_keyword = new String[]{"If", "if (true) {\n\t\n}"};

    String[] try_keyword = new String[]{"Try", "try {\n\t\n}catch(err) {\nconsole.log(err)\n}"};

    String[] class_keyword = new String[]{"Class", "class ClassName {\n" +
            "  constructor() {\n" +
            "  }\n" +
            "}"};

    String[] for_keyword = new String[]{"For", "for (var i = 0; i < 10; i++) {\n\t\n}"};

    String[] while_keyword = new String[]{"While", "while (true) {\n\t\n}"};

    String[] do_keyword =  new String[]{"Do", "do {\n\t\n}}while (true);"};

    String[] switch_keyword = new String[]{"Switch", "switch(expression) {\n" +
            "            case x:\n" +
            "                // code block\n" +
            "                break;\n" +
            "            case y:\n" +
            "                // code block\n" +
            "                break;\n" +
            "            default:\n" +
            "                // code block\n" +
            "        }"};

    String[] catch_keyword = new String[]{"Catch","catch(err) {\nconsole.log(err);\n}"};

    String[] constructor_keyword = new String[]{"Constructor", "constructor()(){\n\t\n}"};

    String[] extends_keyword =  new String[]{"Extends", "extends"};

    String[] implements_keyword =new String[]{"Implements", "implements"};

    String[] var_keyword = new String[]{"Var", "var"};

    String[] let_keyword = new String[]{"Let", "let"};

    String[] const_keyword = new String[]{"Const", "const"};

    String[] static_keyword = new String[]{"Static", "static"};

    String[] private_keyword = new String[]{"Private", "private"};

    String[] public_keyword =  new String[]{"Public", "public"};

    String[] return_keyword = new String[]{"Return", "return"};

    String[] break_keyword = new String[]{"Break", "break"};

    String[] continue_keyword = new String[]{"Continue", "continue"};

    String[] debugger_keyword =  new String[]{"Debugger","debugger"};

    String[] finally_keyword = new String[]{"Finally","finally {\n\n}"};

    String[] throw_keyword = new String[]{"Throw","throw "};

    String[] default_keyword =  new String[]{"Default" , "default:\n\t\n\tbreak;"};

    String[] case_keyword =  new String[]{"Case" , "case:\n\t\n\tbreak;"};

    String[] null_keyword =  new String[]{"Null" , "null"};

    String[] true_keyword =  new String[]{"True" , "true"};

    String[] false_keyword =  new String[]{"False" , "false"};

    String[] instanceOf_Keyword = new String[]{"InstanceOf", "instanceOf"};

}
