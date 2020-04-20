package cn.linkey.rulelib.S006;

import java.util.HashMap;

import cn.linkey.factory.BeanCtx;
import cn.linkey.rule.LinkeyRule;

/**
 * @RuleName:AD域登录认证
 * @author admin
 * @version: 8.0
 * @Created: 2014-05-19 16:02
 */
final public class R_S006_B017 implements LinkeyRule {
    @Override
    public String run(HashMap<String, Object> params) throws Exception {
        //Rdb.setAutoCommit(false);

        String auth = BeanCtx.getRequest().getHeader("Authorization");
        if (auth == null) {
            BeanCtx.getResponse().setStatus(BeanCtx.getResponse().SC_UNAUTHORIZED);
            BeanCtx.getResponse().setHeader("WWW-Authenticate", "NTLM");
            BeanCtx.getResponse().flushBuffer();
            return "";
        }
        if (auth.startsWith("NTLM ")) {
            byte[] msg = new sun.misc.BASE64Decoder().decodeBuffer(auth.substring(5));
            int off = 0, length, offset;
            if (msg[8] == 1) {
                byte z = 0;
                byte[] msg1 = { (byte) 'N', (byte) 'T', (byte) 'L', (byte) 'M', (byte) 'S', (byte) 'S', (byte) 'P', z, (byte) 2, z, z, z, z, z, z, z, (byte) 40, z, z, z, (byte) 1, (byte) 130, z, z, z,
                        (byte) 2, (byte) 2, (byte) 2, z, z, z, z, z, z, z, z, z, z, z, z };
                BeanCtx.getResponse().setHeader("WWW-Authenticate", "NTLM " + new sun.misc.BASE64Encoder().encodeBuffer(msg1));
                BeanCtx.getResponse().sendError(BeanCtx.getResponse().SC_UNAUTHORIZED);
                return "";
            }
            else if (msg[8] == 3) {
                off = 30;
                length = msg[off + 17] * 256 + msg[off + 16];
                offset = msg[off + 19] * 256 + msg[off + 18];
                String remoteHost = new String(msg, offset, length);

                length = msg[off + 1] * 256 + msg[off];
                offset = msg[off + 3] * 256 + msg[off + 2];
                String domain = new String(msg, offset, length);

                length = msg[off + 9] * 256 + msg[off + 8];
                offset = msg[off + 11] * 256 + msg[off + 10];
                String username = new String(msg, offset, length);

                BeanCtx.p("Username:" + username + "<BR>");
                BeanCtx.p("RemoteHost:" + remoteHost + "<BR>");
                BeanCtx.p("Domain:" + domain + "<BR>");
            }
        }

        return "";
    }
}