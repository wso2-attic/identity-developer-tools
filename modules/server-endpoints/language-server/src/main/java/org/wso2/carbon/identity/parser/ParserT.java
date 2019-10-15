package org.wso2.carbon.identity.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import javax.script.ScriptException;
import java.util.*;


public class ParserT {
    private static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON = new Gson();
    private String scope = null;
    private List blocks = new ArrayList();

    public ParserT(){
        scope = null;

    }

    public  List getMyList() {
        return myList;
    }

    private  List myList = new ArrayList();
    public  String toJson(ParseTree tree, boolean prettyPrint ,int line , int charPosition) {
        return prettyPrint ? PRETTY_PRINT_GSON.toJson(toMap(tree , line , charPosition)) : GSON.toJson(toMap(tree , line , charPosition));
    }

    public String toMap(ParseTree tree ,int line , int charPosition) {
        Map<String, Object> map = new LinkedHashMap<>();
        traverse(tree, map, line , charPosition);
//        return String.valueOf(this.blocks);
        if(this.blocks.size() == 0){
            return "Program";
        }else if(String.valueOf(this.blocks.get(0)).equals("Eos") && this.blocks.size() > 0){
            return String.valueOf(this.blocks.get(1));
        }else{
            return String.valueOf(this.blocks.get(0));
        }

    }

    public void traverse(ParseTree tree, Map<String, Object> map , int line , int charPosition) {

        if (tree instanceof TerminalNodeImpl) {
            Token token = ((TerminalNodeImpl) tree).getSymbol();
            map.put("line", token.getLine());
            map.put("column", token.getCharPositionInLine());
            map.put("type",JavaScriptLexer.VOCABULARY.getSymbolicName(token.getType()));
            map.put("text", token.getText());

        }else {
            List<Map<String, Object>> children = new ArrayList<>();
            String name = tree.getClass().getSimpleName().replaceAll("Context$", "");
            map.put(Character.toLowerCase(name.charAt(0)) + name.substring(1), children);
            for (int i = 0; i < tree.getChildCount(); i++) {
                Map<String, Object> nested = new LinkedHashMap<>();
                children.add(nested);
                if(!(tree.getChild(i) instanceof TerminalNodeImpl)){
                    String blockName = tree.getChild(i).getClass().getSimpleName().replaceAll("Context$", "");
                    int startLine , startCharPosition,stopLine,stopCharPosition = 0;
                    startLine = (((ParserRuleContext) tree).start).getLine();
                    startCharPosition = (((ParserRuleContext) tree).start).getCharPositionInLine();
                    if((((ParserRuleContext) tree).stop)!= null){
                        stopLine = (((ParserRuleContext) tree).stop).getLine();
                        stopCharPosition = (((ParserRuleContext) tree).stop).getCharPositionInLine();
                    }else {
                        stopLine = line;
                        stopCharPosition = charPosition+1;
                    }
                    if (((startLine < line) || (startLine == line && startCharPosition <= charPosition)) && ((stopLine > line) || (stopLine == line && stopCharPosition >= charPosition))) {
                        this.blocks.add(0,blockName);
                    }
                    traverse(tree.getChild(i), nested, line, charPosition);
                }
            }

        }

    }

    public  String generateParseTree(String code , int line , int charPosition) throws ScriptException {
        try{
            CharStream charStream = CharStreams.fromString(code);
            JavaScriptLexer javaScriptLexer =new JavaScriptLexer(charStream);
            CommonTokenStream commonTokenStream = new CommonTokenStream(javaScriptLexer);
            JavaScriptParser javaScriptParser = new JavaScriptParser(commonTokenStream);
            ParseTree parseTree = javaScriptParser.program();
            return this.toMap(parseTree,line,charPosition);
        }catch (Exception e){
           System.out.println(e);

        }
        return "Program";
    }

    public void errors(String code){
        CharStream charStream = CharStreams.fromString(code);
        JavaScriptLexer javaScriptLexer =new JavaScriptLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(javaScriptLexer);
        JavaScriptParser javaScriptParser = new JavaScriptParser(commonTokenStream);
        javaScriptParser.removeErrorListeners();
        JavaScriptErrorListner javaScriptErrorListner = new JavaScriptErrorListner();
        javaScriptParser.addErrorListener(javaScriptErrorListner);
        JsonObject object = new JsonObject();

    }

}
