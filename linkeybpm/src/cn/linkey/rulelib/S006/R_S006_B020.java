package cn.linkey.rulelib.S006;

import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:AD域用户名密码验证
 * @author admin
 * @version: 8.0
 * @Created: 2015-12-22 10:28
 */
final public class R_S006_B020 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        // params为运行本规则时所传入的参数
        String userName = "";
        String password = "";
        String host = "10.156.129.72"; // AD服务器IP（一共2个AD域服务器，备份的效果，另一个是10.156.129.95）
        String port = "389"; // 端口
        String DN_OU = "OU=Capitaland China,OU=CCH,DC=capitaland,DC=com,DC=cn";
        String DN_CN = "CN=" + userName;
        String url = new String("ldap://" + host + ":" + port);
        Hashtable env = new Hashtable();
        DirContext ctx;
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            ctx = new InitialDirContext(env);// 初始化上下文
            System.out.println("认证成功");
            ctx.close();
            return userName; // 验证成功返回name
        }
        catch (javax.naming.AuthenticationException e) {
            System.out.println("认证失败");
            System.out.println("e.getExplanation():" + e.getExplanation());
            System.out.println("e.getMessage():" + e.getMessage());
            return "";
        }
        catch (Exception e) {
            System.out.println("认证出错：" + e);
            return "";
        }

    }
}