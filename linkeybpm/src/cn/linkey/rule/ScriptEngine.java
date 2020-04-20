package cn.linkey.rule;

import javax.script.ScriptEngineManager;

import bsh.Interpreter;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;
import cn.linkey.wf.ProcessEngine;

/**
 * 本类为脚本引擎类,主要负责执行简单的Java脚本语句 
 * 
 * 这些语句不是以规则的形式存在的简单脚本
 * 
 * @author Administrator 本类为单例类
 */
public class ScriptEngine {

    /**
     * 执行标准Java语法
     * 
     * @param javaCode
     * @return
     */
    private Object evalCondition(String javaCode) {
        Interpreter shell = new Interpreter(); // 构造一个解释器

        ProcessEngine linkeywf = BeanCtx.getLinkeywf();
        try {
            String importCode = "import cn.linkey.dao.Rdb;import cn.linkey.org.LinkeyUser;import cn.linkey.factory.BeanCtx;";
            shell.set("doc", linkeywf.getDocument());
            shell.set("linkeywf", linkeywf);
            if (javaCode.indexOf("return ") == -1) {
                // 说明是简单的条件语句
                javaCode = " cint(String i){return Integer.parseInt(i);} if(" + javaCode + "){return true;}else{return false;}";
            }
            javaCode = importCode + javaCode;
            return shell.eval(javaCode);

        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "脚本执行错误!");
            BeanCtx.log("E", javaCode);
        }

        return false;
    }

    /**
     * 执行JavaScript语法的条件表达式，成功返回ture,不成立返回false;
     * 
     * @param condition JavaScript语法的条件表达式
     * @return 成立返回true,不成立返回false
     */
    public boolean evalCondition(Document doc, String condition) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
        engine.put("doc", doc);
        engine.put("userid", BeanCtx.getUserid());
        String javaVersion = System.getProperty("java.version");
        String importCode = "";
        if (javaVersion.indexOf("1.8") != -1) {
            importCode = "load(\"nashorn:mozilla_compat.js\");importClass(Packages.cn.linkey.doc.Document);importClass(Packages.cn.linkey.dao.Rdb);importClass(Packages.cn.linkey.factory.BeanCtx);importClass(Packages.cn.linkey.util.Tools);importClass(Packages.cn.linkey.util.DateUtil);importClass(Packages.cn.linkey.org.LinkeyUser);importPackage(Packages.cn.linkey.wf);";
        }
        else {
            importCode = "importClass(Packages.cn.linkey.doc.Document);importClass(Packages.cn.linkey.dao.Rdb);importClass(Packages.cn.linkey.factory.BeanCtx);importClass(Packages.cn.linkey.util.Tools);importClass(Packages.cn.linkey.util.DateUtil);importClass(Packages.cn.linkey.org.LinkeyUser);importPackage(Packages.cn.linkey.wf);";
        }
        String defaultCode = "function cint(str){return parseInt(str);}";
        String evalCode = importCode + defaultCode + "var result=\"0\";if(" + condition + "){result=\"1\";}";
        try {
            engine.eval(evalCode);
            String b = (String) engine.get("result");
            if (b.equals("1")) {
                return true;
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "执行条件计算时出错(" + condition + ")!");
        }

        return false;
    }

    /**
     * 执行路由条件中的代码，成功返回ture,不成立返回false;
     * 
     * @param javascript code
     * @return
     */
    public boolean evalRouterCondition(String condition) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
        engine.put("doc", BeanCtx.getLinkeywf().getDocument());
        engine.put("linkeywf", BeanCtx.getLinkeywf());
        engine.put("userid", BeanCtx.getUserid());
        String javaVersion = System.getProperty("java.version");
        String importCode = "";
        if (javaVersion.indexOf("1.8") != -1) {
            importCode = "load(\"nashorn:mozilla_compat.js\");importClass(Packages.cn.linkey.doc.Document);importClass(Packages.cn.linkey.dao.Rdb);importClass(Packages.cn.linkey.factory.BeanCtx);importClass(Packages.cn.linkey.util.Tools);importClass(Packages.cn.linkey.util.DateUtil);importClass(Packages.cn.linkey.org.LinkeyUser);importPackage(Packages.cn.linkey.wf);";
        }
        else {
            importCode = "importClass(Packages.cn.linkey.doc.Document);importClass(Packages.cn.linkey.dao.Rdb);importClass(Packages.cn.linkey.factory.BeanCtx);importClass(Packages.cn.linkey.util.Tools);importClass(Packages.cn.linkey.util.DateUtil);importClass(Packages.cn.linkey.org.LinkeyUser);importPackage(Packages.cn.linkey.wf);";
        }
        String defaultCode = "function cint(str){return parseInt(str);}";
        String evalCode = importCode + defaultCode + "var result=\"0\";if(" + condition + "){result=\"1\";}";
        try {
            engine.eval(evalCode);
            String b = (String) engine.get("result");
            if (b.equals("1")) {
                return true;
            }
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "执行路由条件计算时出错(" + condition + ")!");
        }

        return false;
    }

    /**
     * 通过Rhino执行JavaScript格式的代码
     * 
     * @param JavaScript Code
     * @return
     */
    public javax.script.ScriptEngine evalJavaScript(String jsCode) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        javax.script.ScriptEngine engine = mgr.getEngineByName("JavaScript");
        engine.put("doc", BeanCtx.getLinkeywf().getDocument());
        engine.put("linkeywf", BeanCtx.getLinkeywf());
        String importCode = "importClass(Packages.cn.linkey.doc.Document);"
                            + "importClass(Packages.cn.linkey.dao.Rdb);" 
                            + "importClass(Packages.cn.linkey.factory.BeanCtx);"
                            + "importClass(Packages.cn.linkey.org.LinkeyUser);" 
                            + "importPackage(Packages.cn.linkey.wf);";
        String evalCode = importCode + jsCode;
        try {
            engine.eval(evalCode);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "执行脚本时出错!");
            BeanCtx.log("E", "错误脚本=" + jsCode);
        }
        return engine;
    }

    /**
     * 使用BeanShell执行标准Java代码
     * 
     * @param javaCode 要执行的java代码
     * @return 返回对像，可以使用强制类型转换来实现返回结果的类型转换
     */
    public Object evalJavaCode(String javaCode) {
        try {
            Interpreter shell = new Interpreter(); // 构造一个解释器
            return shell.eval(javaCode);
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "脚本执行错误!");
            BeanCtx.log("E", javaCode);
        }
        return null;
    }
}
