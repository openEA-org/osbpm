package cn.linkey.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Vml2Svg {

    /**
     * @param args
     */
    private static String GoalVmlBody;
    private static String GoalAreaXml;
    private static Long maxx = 692L;
    private static Long maxy = 630L;
    private static String SvgHead;

    private static String ToSvgHead() {
        String ToSvgHead = "<?xml version=\"1.0\" standalone=\"no\"?>\n";
        ToSvgHead = ToSvgHead + "<svg id=\"svg\" width=\"" + (maxx + 100) + "px\" height=\"" + (maxy + 100) + "px\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n";
        ToSvgHead = ToSvgHead + "<defs>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"StartNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#0c0\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"EndNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#fc3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"Event\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#f0f0f0\"/><stop offset=\"95%\" stop-color=\"#f0f0f0\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"Activity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#daeef3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"SubProcess\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#daeef3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"Area\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#fffff7\"/><stop offset=\"95%\" stop-color=\"#fffff7\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"AutoActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#f0f0f0\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"CurrentActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#ff0000\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"CurrentSubProcess\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#ff0000\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"EndActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#00ff00\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<linearGradient id=\"shape\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#bebebe\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        ToSvgHead = ToSvgHead + "<marker id=\"markerEndArrow\" viewBox=\"0 0 30 30\" refX=\"22\" refY=\"12.5\" markerUnits=\"strokeWidth\" markerWidth=\"12\" markerHeight=\"30\" orient=\"auto\">\n";
        ToSvgHead = ToSvgHead + "<path style=\"stroke-width:1;stroke:black;fill:black;opacity:1\" d=\"M0.3125 0.625 9.3125 12.625 0.3125 24.625 21.3125 12.625 Z\" />\n";
        ToSvgHead = ToSvgHead + "</marker></defs>\n";
        return ToSvgHead;
    }

    private static String unSetCode(String getStr) {
        // 取消代码的混合
        String unSetCode = "";
        unSetCode = getStr.replace("_linkey1", " ");
        unSetCode = getStr.replace("_linkey2", "polyline");
        unSetCode = getStr.replace("_linkey3", "SetProperty");
        unSetCode = getStr.replace("_linkey4", "TextBox");
        unSetCode = getStr.replace("_linkey5", "shapetype");
        unSetCode = getStr.replace("_linkey6", "<");
        unSetCode = getStr.replace("_linkey7", ">");
        unSetCode = getStr.replace("_linkey8", "VML");
        unSetCode = getStr.replace("_linkey9", "vml");
        return unSetCode;
    }

    private static String ToPolyline(String htmlStr) {
        String ToPolyline = "";
        String ReturnXml = "", points = "";
        String rStr = "", StartStr = "", EndStr = "", VmlStr = "";
        String id = "", x = "", y = "";
        String SvgStr = "", tempStr = "";
        long startpos;
        int max = 0;
        String startcode = "<v:polyline";
        String endcode = "</v:polyline>";
        startpos = htmlStr.indexOf(startcode);
        if (startpos > 0) {
            rStr = htmlStr;
            while (startpos >= 0) {
                max = max + 1;
                if (max > 1000)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode));
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                VmlStr = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode;
                points = GetVal(VmlStr, "oldpoints=\"", "\"");
                points = points.replace("px", "");
                id = GetId(VmlStr);
                x = GetX(VmlStr);
                y = GetY(VmlStr);
                if (Long.parseLong(x) > maxx) {
                    maxx = Long.parseLong(x);
                }
                if (Long.parseLong(y) > maxy) {
                    maxy = Long.parseLong(y);
                }
                Long i = Long.parseLong(x);
                Long j = Long.parseLong(y);
                i = i - 23;
                j = j - 27;
                SvgStr = "<polyline id=\"" + id + "\" transform=\"translate(" + i + "," + j + ")\"";
                SvgStr = SvgStr + " points=\"" + points + "\" stroke-width=\"1\" style=\"fill:none;stroke:black;marker-end:url(#markerEndArrow);\"  />";
                ReturnXml = ReturnXml + "\n" + SvgStr;
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                rStr = EndStr;
                tempStr = tempStr + StartStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr = tempStr + rStr;
        }
        else
            tempStr = htmlStr;
        GoalVmlBody = tempStr;
        ToPolyline = ReturnXml;
        return ToPolyline;
    }

    private static String GetVal(String VmlStr, String StartCode, String EndCode) {
        String GetVal = "";
        String temStr = "";
        temStr = VmlStr.substring(VmlStr.indexOf(StartCode) + StartCode.length(), VmlStr.length());
        GetVal = temStr.substring(0, temStr.indexOf(EndCode));
        return GetVal;
    }

    private static String GetId(String VmlStr) {
        // 获得Nodeid作为id
        String GetId = "";
        String temStr = "";
        temStr = VmlStr.substring(VmlStr.indexOf("Nodeid=") + 8, VmlStr.length());
        GetId = temStr.substring(0, temStr.indexOf("\""));
        return GetId;
    }

    private static String GetX(String VmlStr) {
        String GetX = "";
        String temStr = "";
        temStr = VmlStr.substring(VmlStr.indexOf("LEFT: ") + 6, VmlStr.length());
        GetX = temStr.substring(0, temStr.indexOf("px"));
        return GetX;
    }

    private static String ToCircle(String htmlStr) {
        // 转换开始节点
        String ToCircle = "", x = "", y = "", NodeType = "", NodeText = "";
        String startcode = "", rStr = "", stroke = "", SvgStr = "";
        String endcode = "", StartStr = "", EndStr = "", VmlStr = "";
        String ReturnXml = "", tempStr = "";
        long startpos;
        int max = 0;
        startcode = "<v:Oval";
        endcode = "</v:Oval>";
        startpos = htmlStr.indexOf(startcode);
        if (startpos > 0) {
            rStr = htmlStr;
            while (startpos > 0) {
                if (max > 1000)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode));
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                VmlStr = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode;
                NodeType = GetVal(VmlStr, "NodeType=\"", "\"");
                stroke = "#004d86"; // GetVal(VmlStr,"strokecolor=\"","\"");
                if (NodeType.equalsIgnoreCase("Event")) {
                    // 事件节点
                    x = GetX(VmlStr);
                    y = GetY(VmlStr);
                    Long j = Long.parseLong(y);
                    if (Long.parseLong(x) > maxx) {
                        maxx = Long.parseLong(x);
                    }
                    if (Long.parseLong(y) > maxy) {
                        maxy = Long.parseLong(y);
                    }
                    SvgStr = "<circle  id=\"" + GetId(VmlStr) + "\"" + " cx=\"" + (Integer.valueOf(x) - 10) + "\" cy=\"" + (Integer.valueOf(y) - 15) + "\" r=\"13\" NodeType=\"" + NodeType + "\""
                            + " fill=\"url(#Event)\" stroke=\"" + stroke + "\" stroke-width=\"1.5\" ></circle>";
                    ReturnXml = ReturnXml + "\n" + SvgStr;
                    EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                }
                else {
                    // 开始或结束节点
                    NodeText = GetText(VmlStr);
                    x = GetX(VmlStr);
                    y = GetY(VmlStr);
                    Long j = Long.parseLong(y);
                    if (Long.parseLong(x) > maxx) {
                        maxx = Long.parseLong(x);
                    }
                    if (Long.parseLong(y) > maxy) {
                        maxy = Long.parseLong(y);
                    }
                    SvgStr = "<circle  id=\"" + GetId(VmlStr) + "\"" + " cx=\"" + x + "\" cy=\"" + y + "\" r=\"22.5\" NodeType=\"" + NodeType + "\"" + " fill=\"url(#" + NodeType + ")\" stroke=\""
                            + stroke + "\" stroke-width=\"1.5\" ></circle>";
                    SvgStr = SvgStr + "\n" + "<text x=\"" + x + "\" y=\"" + (j - 0 + 3) + "\"" + " text-anchor=\"middle\" font-size=\"9pt\" fill=\"black\" >" + NodeText + "</text>";
                    ReturnXml = ReturnXml + "\n" + SvgStr;
                    EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                }
                rStr = EndStr;
                tempStr = tempStr + StartStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr = tempStr + rStr;
        }
        else {
            tempStr = htmlStr;
        }
        GoalVmlBody = tempStr;
        ToCircle = ReturnXml;
        return ToCircle;
    }

    private static String GetText(String VmlStr) {
        String tmpStr = "";
        String GetText = "";
        tmpStr = VmlStr.substring(0, VmlStr.indexOf("</v:TextBox>"));
        GetText = tmpStr.substring(tmpStr.lastIndexOf(">") + 1, tmpStr.length());
        return GetText;
    }

    private static String GetY(String VmlStr) {
        String GetY = "";
        String temStr = "";
        temStr = VmlStr.substring(VmlStr.indexOf("TOP: ") + 5, VmlStr.length());
        GetY = temStr.substring(0, temStr.indexOf("px"));
        return GetY;
    }

    private static String ToActivity(String htmlStr) {
        String ToActivity = "";
        String ToCircle = "", x = "", y = "", NodeType = "", NodeText = "";
        String startcode = "", rStr = "", stroke = "", SvgStr = "";
        String endcode = "", StartStr = "", EndStr = "", VmlStr = "", points = "";
        String RWidth = "", RHeight = "", id = "";
        String ReturnXml = "", tempStr = "";
        long startpos;
        int max = 0;
        startcode = "<v:roundrect";
        endcode = "</v:roundrect>";

        startpos = htmlStr.indexOf(startcode);

        if (startpos > 0) {
            rStr = htmlStr;
            while (startpos > 0) {
                max = max + 1;
                if (max > 1000)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode));
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                VmlStr = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode;
                NodeType = GetVal(VmlStr, "NodeType=\"", "\"");
                stroke = "#004d86"; // GetVal(VmlStr,"strokecolor= \"","\"");
                RWidth = GetVal(VmlStr, "WIDTH: ", "px");
                RHeight = GetVal(VmlStr, "HEIGHT: ", "px");
                id = GetId(VmlStr);
                x = GetX(VmlStr);
                y = GetY(VmlStr);
                Long j = Long.parseLong(y);
                Long i = Long.parseLong(x);
                if (Long.parseLong(x) > maxx) {
                    maxx = Long.parseLong(x);
                }
                if (Long.parseLong(y) > maxy) {
                    maxy = Long.parseLong(y);
                }
                NodeText = GetText(VmlStr);
                NodeText = FormatNodeText(NodeText, Long.toString(i - 0 + 25), y);
                SvgStr = "<rect  x=\"" + (i - 23) + "\" y=\"" + (j - 25) + "\" width=\"" + RWidth + "\" height=\"" + RHeight + "\" id=\"" + id + "\" rx=\"10\" ry=\"10\" fill=\"url(#" + NodeType
                        + ")\" stroke=\"" + stroke + "\" stroke-width=\"1.5\" ></rect>\n";
                if (NodeType.equals("Area")) {
                    GoalAreaXml = GoalAreaXml + SvgStr + "\n";
                }
                else {
                    SvgStr = SvgStr + NodeText;

                    ReturnXml = ReturnXml + "\n" + SvgStr;
                }
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                rStr = EndStr;
                tempStr = tempStr + StartStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr = tempStr + rStr;
        }
        else
            tempStr = htmlStr;
        GoalVmlBody = tempStr;
        ToActivity = ReturnXml;
        return ReturnXml;
    }

    private static String ToPolygon(String htmlStr) {
        // 转换share决策点
        String ToPolygon = "", rStr = "", StartStr = "", EndStr = "", stroke = "";
        String startcode = "", endcode = "", VmlStr = "", NodeType = "", NodeText = "";
        String id = "", x = "", y = "", SvgStr = "", ReturnXml = "", tempStr = "";
        long startpos;
        int max = 0;
        startcode = "<v:shape";
        endcode = "</v:shape>";
        startpos = htmlStr.indexOf(startcode);
        if (startpos > 0) {
            rStr = htmlStr;
            while (startpos > 0) {
                max = max + 1;
                if (max > 1000)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode));
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                VmlStr = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode;
                NodeType = GetVal(VmlStr, "NodeType=\"", "\"");
                stroke = GetVal(VmlStr, "strokecolor = \"", "\"");
                NodeText = GetText(VmlStr);
                id = GetId(VmlStr);
                x = GetX(VmlStr);
                y = GetY(VmlStr);
                Long j = Long.parseLong(y);
                Long i = Long.parseLong(x);
                if (Long.parseLong(x) > maxx) {
                    maxx = Long.parseLong(x);
                }
                if (Long.parseLong(y) > maxy) {
                    maxy = Long.parseLong(y);
                }
                if (NodeType.equals("Edge")) {
                    SvgStr = "<polygon id=\"" + id + "\" transform=\"translate(" + (i - 25) + "," + (j - 27)
                            + ")\" points=\"55,0 110,30 55,60 0,30\" stroke=\"#004d86\" stroke-width=\"1.5\" fill=\"url(#shape)\"/>";
                    SvgStr = SvgStr + "\n<text x=\"" + (i - 0 + 30) + "\" y=\"" + (j + 5) + "\"  text-anchor=\"middle\" font-size=\"13\" fill=\"black\" >" + NodeText + "</text>";
                }
                else {
                    NodeText = FormatNodeText(NodeText, Long.toString(i + 35), Long.toString(j));
                    SvgStr = "\n<path id=\"" + id + "\" transform=\"translate(" + (i - 23) + "," + (j - 25)
                            + ")\" d=\"M0 0 L96 0 L96 50 L86 50 L86 0 L86 50 L10 50 L10 0 L10 50 L0 50 Z\" stroke=\"#004d86\" stroke-width=\"1.5\" fill=\"url(#SubProcess)\" ></path>";
                    SvgStr = SvgStr + NodeText;
                }
                ReturnXml = ReturnXml + "\n" + SvgStr;
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                rStr = EndStr;
                tempStr = tempStr + StartStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr = htmlStr;
        }
        GoalVmlBody = tempStr;
        return ReturnXml;
    }

    private static String ToSpan(String htmlStr) {
        String ToSpan = "";
        String rStr = "", StartStr = "", EndStr = "", stroke = "";
        String startcode = "", endcode = "", VmlStr = "", NodeType = "", NodeText = "";
        startcode = "<v:shape";
        String id = "", x = "", y = "", SvgStr = "", ReturnXml = "", tempStr = "";
        long startpos;
        int max = 0;
        startcode = "<SPAN";
        endcode = "</SPAN>";
        startpos = htmlStr.indexOf(startcode);
        if (startpos > 0) {
            rStr = htmlStr;
            while (startpos > 0) {
                max = max + 1;
                if (max > 1000)
                    break;
                StartStr = rStr.substring(0, rStr.indexOf(startcode));
                EndStr = rStr.substring(rStr.indexOf(startcode) + startcode.length(), rStr.length());
                VmlStr = startcode + EndStr.substring(0, EndStr.indexOf(endcode)) + endcode;
                if (VmlStr.indexOf("<BR>") == -1) {
                    id = GetId(VmlStr);
                    x = GetX(VmlStr);
                    y = GetY(VmlStr);
                    Long j = Long.parseLong(y);
                    Long i = Long.parseLong(x);
                    if (Long.parseLong(x) > maxx) {
                        maxx = Long.parseLong(x);
                    }
                    if (Long.parseLong(y) > maxy) {
                        maxy = Long.parseLong(y);
                    }
                    String temp = VmlStr.substring(VmlStr.indexOf(">") + 1, VmlStr.length());
                    NodeText = temp.substring(0, temp.indexOf("</SPAN>"));
                    SvgStr = "<text x=\"" + (i - 23) + "\" y=\"" + (j - 17) + "\"  font-size=\"9pt\" fill=\"black\" >" + NodeText + "</text>";
                }
                else {
                    id = GetId(VmlStr);
                    x = GetX(VmlStr);
                    y = GetY(VmlStr);
                    Long j = Long.parseLong(y);
                    Long i = Long.parseLong(x);
                    if (Long.parseLong(x) > maxx) {
                        maxx = Long.parseLong(x);
                    }
                    if (Long.parseLong(y) > maxy) {
                        maxy = Long.parseLong(y);
                    }
                    String temp = VmlStr.substring(VmlStr.indexOf(">") + 1, VmlStr.length());
                    NodeText = temp.substring(0, temp.indexOf("</SPAN>"));
                    NodeText = ToSpanBr(NodeText, Long.toString(i - 23));
                    SvgStr = "<text x=\"" + (i - 23) + "\" y=\"" + (j - 17) + "\"  font-size=\"9pt\" fill=\"black\" >" + NodeText + "</text>";
                }
                ReturnXml = ReturnXml + "\n" + SvgStr;
                EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
                rStr = EndStr;
                tempStr = tempStr + StartStr;
                startpos = rStr.indexOf(startcode);
            }
            tempStr = tempStr + rStr;
        }
        else
            tempStr = htmlStr;
        GoalVmlBody = tempStr;
        return ReturnXml;
    }

    private static String ToSpanBr(String SpanStr, String x) {
        String ToSpanBr = "";
        String startcode = "", rStr = "", FirstFlag = "";
        int startpos = 0, max = 0;
        String tempStr = "", StartStr = "";
        startcode = "<BR>";
        FirstFlag = "1";
        startpos = SpanStr.indexOf(startcode);
        if (startpos > 0) {
            rStr = SpanStr;
            while (startpos > 0 && max < 100) {
                StartStr = rStr.substring(0, rStr.indexOf("<BR>"));
                if (FirstFlag.equals("1")) {
                    tempStr = tempStr + StartStr + "<tspan x=\"" + x + "\" dy=\"20\">";
                    FirstFlag = "0";
                }
                else
                    tempStr = tempStr + StartStr + "</tspan><tspan x=\"" + x + "\" dy=\"20\">";
                rStr = rStr.substring(rStr.indexOf("<BR>") + 4, rStr.length());
                startpos = rStr.indexOf(startcode);
                max = max + 1;
            }
            tempStr = tempStr + rStr + "</tspan>";
        }
        else
            tempStr = SpanStr;
        ToSpanBr = tempStr;
        return ToSpanBr;
    }

    private static String FormatNodeText(String NodeText, String x, String y) {
        String FormatNodeText = "";
        boolean ISCN = false;
        int MaxNum;
        String StartStr = "", NextStr = "";
        int Num = NodeText.length();
        int i;
        Long j = Long.parseLong(y);
        Long k = Long.parseLong(x);
        for (i = 1; i <= Num;) {
            String c = NodeText.substring(i, i + 1);
            char a = c.charAt(0);
            int p = (int) a;
            if (p < 255) {
                ISCN = false;
                break;
            }
            else {
                ISCN = true;
                i = Num + 2;
                break;
            }
        }
        // 是中文情况
        if (ISCN) {
            MaxNum = 7;
            if (Num > MaxNum) {
                StartStr = NodeText.substring(0, MaxNum);
                NextStr = NodeText.substring(NodeText.length() - Num + MaxNum, NodeText.length());
                if (NextStr.length() > 7) {
                    NextStr = NextStr.substring(0, MaxNum);
                }
                NextStr = "<tspan x=\"" + (k - 46) + "\" dy=\"15\">" + NextStr + "</tspan>";
                FormatNodeText = "<text x=\"" + (k - 46) + "\" y=\"" + (j - 5) + "\" font-size=\"13\" font-family=\"宋体\" >" + StartStr + NextStr + "</text>";
            }
            else {
                FormatNodeText = "<text x=\"" + x + "\" y=\"" + y + "\"  text-anchor=\"middle\" font-size=\"13\" font-family=\"宋体\" >" + NodeText + "</text>";
            }
        }
        else {
            MaxNum = 15;
            if (Num > MaxNum) {
                StartStr = NodeText.substring(0, MaxNum);
                NextStr = NodeText.substring(NodeText.length() - Num + MaxNum, NodeText.length());
                if (NextStr.length() > MaxNum) {
                    NextStr = NextStr.substring(0, MaxNum);
                }
                NextStr = "<tspan x=\"" + (k - 46) + "\" dy=\"15\">" + NextStr + "</tspan>";
                FormatNodeText = "<text x=\"" + (k - 46) + "\" y=\"" + (j - 5) + "\"   font-size=\"13\" font-family=\"Arial\" >" + StartStr + NextStr + "</text>";
            }
            else {
                FormatNodeText = "<text x=\"" + x + "\" y=\"" + y + "\"  text-anchor=\"middle\" font-size=\"13\" font-family=\"Arial\" >" + NodeText + "</text>";
            }
        }
        return FormatNodeText;
    }

    public static String getSvgXml(String VmlStr) {
        String GetSvgXml = "";
        VmlStr = unSetCode(VmlStr);
        GoalVmlBody = VmlStr;
        GoalVmlBody = VmlStr;
        GetSvgXml = GetSvgXml + ToPolyline(GoalVmlBody);
        GetSvgXml = GetSvgXml + ToCircle(GoalVmlBody);
        GetSvgXml = GetSvgXml + ToActivity(GoalVmlBody);
        GetSvgXml = GetSvgXml + ToPolygon(GoalVmlBody);
        GetSvgXml = GetSvgXml + ToSpan(GoalVmlBody);
        SvgHead = ToSvgHead();
        GetSvgXml = SvgHead + GoalAreaXml + GetSvgXml + "</svg>";
        return GetSvgXml;
    }
    
    /**
     * @Description: 对SVG流程图特殊处理，解决新版SVG流程图无法查看问题。
     * @param:新版SVG的节点描述
     * @return：添加svg的声明
     * @author: Alibao
     * @date: 2018年1月9日 下午10:32:58
      */
     public static String getSvgXml2(String SVGStr) {
         String getSvgXml = "";
         String svgHead = "<svg id=\"svg\" width=\"100%\" height=\"100%\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">";
         getSvgXml = svgHead + SVGStr + "</svg>";
         return getSvgXml;
     }


    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        List<String> lines = new ArrayList<String>();
        // 读取源代码文件
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\vml源码.txt"), "gbk"));
        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        String arr = lines.toString();
        // 输出转换后的代码
        System.out.println(Vml2Svg.getSvgXml(arr));
    }

}
