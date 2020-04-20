package cn.linkey.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.linkey.dao.Rdb;

public class VmlToSvg {

	/**
	 * @param args
	 */
	private static String GoalVmlBody;
	private static String GoalAreaXml;
	private static Long maxx = 692L;
	private static Long maxy = 630L;
	private static String SvgHead;

	private static String ToSvgHead() {
		String ToSvgHead = "";
		//ToSvgHead = ToSvgHead + "<defs>\n";
		/*ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"StartNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#0c0\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"EndNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#fc3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"Event\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#f0f0f0\"/><stop offset=\"95%\" stop-color=\"#f0f0f0\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"Activity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#daeef3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
	
        ToSvgHead = ToSvgHead + "<linearGradient id=\"SubProcess\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#daeef3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
       
        ToSvgHead = ToSvgHead + "<linearGradient id=\"OutProcess\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#daeef3\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        
        ToSvgHead = ToSvgHead + "<linearGradient id=\"SNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
        ToSvgHead = ToSvgHead + "<stop offset=\"5%\" stop-color=\"#bebebe\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
        
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"Area\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#fffff7\"/><stop offset=\"95%\" stop-color=\"#fffff7\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"AutoActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#f0f0f0\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"CurrentActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#ff0000\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"CurrentSubProcess\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#ff0000\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<linearGradient id=\"EndActivity\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
		ToSvgHead = ToSvgHead
				+ "<stop offset=\"5%\" stop-color=\"#00ff00\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
		ToSvgHead = ToSvgHead
				+ "<marker id=\"markerEndArrow\" viewBox=\"0 0 33 30\" refX=\"22\" refY=\"12.5\" markerUnits=\"strokeWidth\" markerWidth=\"12\" markerHeight=\"30\" orient=\"auto\">\n";
		ToSvgHead = ToSvgHead
				+ "<path fill=\"black\" stroke-width=\"1\" stroke=\"black\" opacity=\"1\" d=\"M6.3125 4.625 15.3125 12.625 6.3125 20.625 21.3125 12.625 Z\" />\n";
		ToSvgHead = ToSvgHead + "</marker></defs>\n";*/
		
//		ToSvgHead = ToSvgHead//
//				+ "<linearGradient id=\"XNode_rect\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
//		ToSvgHead = ToSvgHead
//				+ "<stop offset=\"5%\" stop-color=\"#ccc\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
//		ToSvgHead = ToSvgHead//
//				+ "<linearGradient id=\"XNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
//		ToSvgHead = ToSvgHead
//				+ "<stop offset=\"5%\" stop-color=\"#f4f4f4\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
//		ToSvgHead = ToSvgHead//
//				+ "<linearGradient id=\"YNode_rect\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
//		ToSvgHead = ToSvgHead
//				+ "<stop offset=\"5%\" stop-color=\"#ccc\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
//		ToSvgHead = ToSvgHead//
//				+ "<linearGradient id=\"YNode\" spreadMethod=\"repeat\" gradientTransform=\"rotate(270)\">\n";
//		ToSvgHead = ToSvgHead
//				+ "<stop offset=\"5%\" stop-color=\"#f4f4f4\"/><stop offset=\"95%\" stop-color=\"white\"/></linearGradient>\n";
//		ToSvgHead = ToSvgHead + "</defs>\n";
	
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
		String ondblclick = "";
		String Nodeid = "";
		String hotType = "";
		String PolyLineType = "";
		String TargetNode = "";
		String SourceNode = "";
		String SaveFlag = "";
		String LinkeyNode = "";
		String oldpoints = "";
		String orpoints = "";
		String SvgStr = "", tempStr = "";
		String NodeText = "";
		String NodeType = "";
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
				points = GetVal(VmlStr, "points=\"", "\"");
				oldpoints = GetVal(VmlStr, "oldpoints=\"", "\"");
				orpoints = GetVal(VmlStr, "orpoints=\"", "\"");
				ondblclick = GetVal(VmlStr, "ondblclick=\"", "\"");
				Nodeid = GetVal(VmlStr, "Nodeid=\"", "\"");
				hotType = GetVal(VmlStr, "hotType=\"", "\"");
				PolyLineType = GetVal(VmlStr, "PolyLineType=\"", "\"");
				TargetNode = GetVal(VmlStr, "TargetNode=\"", "\"");
				SourceNode = GetVal(VmlStr, "SourceNode=\"", "\"");
				SaveFlag = GetVal(VmlStr, "SaveFlag=\"", "\"");
				LinkeyNode = GetVal(VmlStr, "LinkeyNode=\"", "\"");
				points = points.replace("px", "").replace("pt", "");
				oldpoints = oldpoints.replace("px", "");
				orpoints = orpoints.replace("px", "");

				id = GetId(VmlStr);
				NodeText = getPolyLineText(htmlStr, id);
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

				// 用points来定位
				points = getPoint(points, i, j);
				oldpoints = getPoint(oldpoints, i, j);
				orpoints = getPoint(orpoints, i, j);
				String str1[] = points.split(" ");
				i = (Long.parseLong(str1[1].split(",")[0]) + Long.parseLong(str1[str1.length - 2].split(",")[0])) / 2;
				j = (Long.parseLong(str1[1].split(",")[1]) + Long.parseLong(str1[str1.length - 2].split(",")[1])) / 2;
				SvgStr = "<polyline id=\"" + id;
				SvgStr = SvgStr + "\" Nodeid=\"" + Nodeid + "\" hotType=\"" + hotType + "\" ondblclick=\"" + ondblclick
						+ "\" PolyLineType=\"" + PolyLineType + "\" TargetNode=\"" + TargetNode + "\" SourceNode=\""
						+ SourceNode + "\" LinkeyNode=\"" + LinkeyNode + "\" points=\"" + points + "\" orpoints=\""
						+ orpoints + "\" oldpoints=\"" + oldpoints
						+ "\" stroke-width=\"1.5\" fill=\"none\" stroke=\"black\" marker-end=\"url(#markerEndArrow)\"  />";
				SvgStr = SvgStr + "\n" + "<text x=\"" + i + "\" id=\"" + id + "_text" + "\" Nodeid=\"" + Nodeid
						+ "_text" + "\" ondblclick=\"" + ondblclick + "\" y=\"" + (j) + "\""
						+ " text-anchor=\"middle\" font-size=\"9pt\" fill=\"black\" >" + NodeText + "</text>\n";
				ReturnXml = ReturnXml + "\n" + SvgStr;
				EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else
			tempStr = htmlStr;
		GoalVmlBody = tempStr;
		ToPolyline = ReturnXml;
		return ToPolyline;
	}

	// vml线段定位是left，top加points，转svg定位，用points
	private static String getPoint(String str, long x, long y) {
		String pointStr = "";
		if (str.equals(""))
			return "";
		String[] points = str.split(" ");
		String[] point = new String[2];
		for (int i = 0; i < points.length; i++) {
			point = points[i].split(",");
			long xx = (long) (Float.valueOf(point[0]) + x);
			long yy = (long) (Float.valueOf(point[1]) + y);
			points[i] = xx + "," + yy + " ";
			pointStr += points[i];
		}

		// System.out.println("points:" + pointStr.trim());
		return pointStr.trim();
	}

	private static String GetVal(String VmlStr, String StartCode, String EndCode) {
		String GetVal = "";
		String temStr = "";
		if (VmlStr.indexOf(StartCode) == -1)
			return "";
		temStr = VmlStr.substring(VmlStr.indexOf(StartCode) + StartCode.length(), VmlStr.length());
		GetVal = temStr.substring(0, temStr.indexOf(EndCode));
		return GetVal;
	}

	private static String GetId(String VmlStr) {
		// 获得Nodeid作为id
		String GetId = "";
		String temStr = "";
		if (VmlStr.indexOf("id=") > -1) {
			temStr = VmlStr.substring(VmlStr.indexOf("id=") + 3, VmlStr.length());
			GetId = temStr.substring(0, temStr.indexOf(" "));
			return GetId;
		}
		return null;
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
		String NodeNum = "";
		String Nodeid = "";
		String ondblclick = "";
		String LinkeyEndObj = "";
		String LinkeyStartObj = "";
		String SaveFlag = "";
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
				ondblclick = GetVal(VmlStr, "ondblclick=\"", "\"");
				NodeType = GetVal(VmlStr, "NodeType=\"", "\"").trim();
				Nodeid = GetVal(VmlStr, "Nodeid=\"", "\"").trim();
				NodeNum = GetVal(VmlStr, "NodeNum=\"", "\"").trim();
				LinkeyEndObj = GetVal(VmlStr, "LinkeyEndObj=\"", "\"").trim();
				LinkeyStartObj = GetVal(VmlStr, "LinkeyStartObj=\"", "\"").trim();
				SaveFlag = GetVal(VmlStr, "SaveFlag=\"", "\"").trim();
				stroke = "#004d86"; // Ge1tVal(VmlStr,"strokecolor=\"","\"");
				if (NodeType.equals("Event")) {
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
					SvgStr = "<circle  id=\"" + GetId(VmlStr) + "\"" + " cx=\"" + (Integer.valueOf(x) - 10) + "\" cy=\""
							+ (Integer.valueOf(y) - 15) + "\" r=\"13\" NodeType=\"" + NodeType + "\" LinkeyEndObj=\""
							+ LinkeyEndObj + "\" LinkeyStartObj=\"" + LinkeyStartObj + "\" Nodeid=\"" + Nodeid
							+ "\" SaveFlag=\"" + SaveFlag + "\" NodeNum=\"" + NodeNum + "\" ondblclick=\"" + ondblclick
							+ "\" " + " fill=\"url(#Event)\" stroke=\"" + stroke + "\" stroke-width=\"1.5\" ></circle>";
					ReturnXml = ReturnXml + "\n" + SvgStr;
					EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				} else {
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
					SvgStr = "<circle  id=\"" + GetId(VmlStr) + "\"" + " NodeType=\"" + NodeType + "\" Nodeid=\""
							+ Nodeid + "\" NodeNum=\"" + NodeNum + "\" LinkeyEndObj=\"" + LinkeyEndObj
							+ "\" LinkeyStartObj=\"" + LinkeyStartObj + "\" SaveFlag=\"" + SaveFlag + "\" cx=\"" + x
							+ "\" cy=\"" + y + "\" r=\"22.5\" NodeType=\"" + NodeType + "\"" + " fill=\"url(#"
							+ NodeType + ")\" stroke=\"" + stroke + "\" ondblclick=\"" + ondblclick
							+ "\" stroke-width=\"1.5\" ></circle>\n";
					SvgStr = SvgStr + "\n" + "<text x=\"" + x + "\" id=\"" + GetId(VmlStr) + "_text" + "\" Nodeid=\""
							+ Nodeid + "_text" + "\" NodeType=\"" + NodeType + "\" y=\"" + (j - 0 + 3) + "\""
							+ " text-anchor=\"middle\" font-size=\"9pt\" fill=\"black\" >" + NodeText + "</text>\n";
					ReturnXml = ReturnXml + "\n" + SvgStr;
					EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				}
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else {
			tempStr = htmlStr;
		}
		GoalVmlBody = tempStr;
		ToCircle = ReturnXml;
		return ToCircle;
	}

	private static String GetText(String VmlStr) {
		String tmpStr = "";
		String GetText = "";
		if (VmlStr.indexOf("</v:TextBox>") > -1) {
			tmpStr = VmlStr.substring(0, VmlStr.indexOf("</v:TextBox>"));
		}
		GetText = tmpStr.substring(tmpStr.lastIndexOf(">") + 1, tmpStr.length());
		return GetText;
	}

	private static String getPolyLineText(String htmlStr, String pid) {
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
					int index = VmlStr.indexOf("id=");
					String id1 = VmlStr.substring(index + 10, index + 29);
					if (pid != null && pid.equals(id1)) {
						NodeText = temp.substring(0, temp.indexOf("</SPAN>"));
					} else
						NodeText = "";
					SvgStr = NodeText;

				} else {
					String temp = VmlStr.substring(VmlStr.indexOf(">") + 1, VmlStr.length());
					int index = VmlStr.indexOf("id=");
					String id1 = VmlStr.substring(index + 10, index + 29);
					if (pid != null && pid.equals(id1)) {
						NodeText = temp.substring(0, temp.indexOf("</SPAN>"));
					} else
						NodeText = "";
					SvgStr = NodeText;
				}
				ReturnXml = ReturnXml + SvgStr;
				EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else
			tempStr = htmlStr;
		GoalVmlBody = tempStr;
		return ReturnXml;
	};

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
		String Nodeid = "";
		String ondblclick = "";
		String LinkeyEndObj = "";
		String LinkeyStartObj = "";
		String NodeNum = "";
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
				NodeNum = GetVal(VmlStr, "NodeNum=\"", "\"");
				Nodeid = GetVal(VmlStr, "Nodeid=\"", "\"");
				ondblclick = GetVal(VmlStr, "ondblclick=\"", "\"");
				LinkeyEndObj = GetVal(VmlStr, "LinkeyEndObj=\"", "\"");
				LinkeyStartObj = GetVal(VmlStr, "LinkeyStartObj=\"", "\"");
				stroke = "#004d86"; // Ge1tVal(VmlStr,"strokecolor=\"","\"");
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
				String str = " Nodeid=\"" + Nodeid + "_text\"" + " id=\"" + id + "_text\"" + " NodeType=\"" + NodeType
						+ "\"" + " ondblclick=\"" + ondblclick + "\"";
				NodeText = FormatNodeText(NodeText, str, Long.toString(i + 25), Long.toString(j + 5));
				// System.out.println("text=" + NodeText);
				SvgStr = "<rect Nodeid=\"" + Nodeid + "\" NodeType=\"" + NodeType + "\" " + " ondblclick=\""
						+ ondblclick + "\" class=" + NodeType + " NodeNum=\"" + NodeNum + " \"LinkeyStartObj=\""
						+ LinkeyStartObj + "\" LinkeyEndObj=\"" + LinkeyEndObj + "\" x=\"" + (i - 23) + "\" y=\""
						+ (j - 25) + "\" width=\"" + RWidth + "\" height=\"" + RHeight + "\" id=\"" + id
						+ "\" rx=\"10\" ry=\"10\" fill=\"url(#" + NodeType + ")\" stroke=\"" + stroke
						+ "\" stroke-width=\"1.5\" ></rect>\n";
				if (NodeType.equals("Area")) {
					GoalAreaXml = GoalAreaXml + SvgStr + "\n";
				} else {
					SvgStr = SvgStr + NodeText;

					ReturnXml = ReturnXml + "\n" + SvgStr;
				}
				EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else
			tempStr = htmlStr;
		GoalVmlBody = tempStr;
		ToActivity = ReturnXml;
		return ReturnXml;
	}

	private static String ToXYNode(String htmlStr) {
		String ToActivity = "";
		String ToCircle = "", x = "", y = "", NodeType = "", NodeText = "";
		String startcode = "", rStr = "", stroke = "", SvgStr = "";
		String endcode = "", StartStr = "", EndStr = "", VmlStr = "", points = "";
		String RWidth = "", RHeight = "", id = "";
		String onmousedown = "\"flowtableondblclick1()\"";// flowtableonmousedown1()
		String NodeNum = "";
		String ReturnXml = "", tempStr = "";
		long startpos;
		int max = 0;
		startcode = "<v:rect";
		endcode = "</v:rect>";

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
				NodeNum = GetVal(VmlStr, "NodeNum=\"", "\"");
				stroke = "#999999"; // Ge1tVal(VmlStr,"strokecolor=\"","\"");
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
				String id1 = id;
				if (id.substring(4, id.length()).length() > 4) {
					id1 = (Integer.parseInt(id.substring(4, id.length())) - 10000) + "";
				}
				String str = " id=\"Node" + id1 + "_xtb\"" + " NodeType=\"" + NodeType + "\"" + " onmousedown="
						+ onmousedown;

				// NodeText = FormatNodeText(NodeText, str,
				// Long.toString(i),(Integer.parseInt(y)+45)+"");
				// System.out.println("text=" + NodeText);
				if ("XNode_text".equals(NodeType)) {
					NodeType = "XNode_rect";
					str += " font-weight=\"bold\" font-size=\"9pt\" writing-mode=\"tb-rl\" text-algin=\"center\"";
					NodeText = FormatNodeText(NodeText, str, Long.toString(i + 10), (Integer.parseInt(y) + 80) + "");
				} else if ("YNode_text".equals(NodeType)) {
					NodeType = "YNode_rect";
					NodeText = FormatNodeText(NodeText, str, Long.toString(i + 80), (Integer.parseInt(y) + 20) + "");
				}
				
				//String urdType = NodeType.substring(0, 5)+"_text";

				SvgStr = "<rect" + " NodeType=\"" + NodeType + "\" " + " onmousedown=\"flowtableonmousedown1()\""
						+ " NodeNum=\"" + NodeNum + " \"" + "x=\"" + (i) + "\" y=\"" + (j) + "\" width=\"" + RWidth
						+ "\" height=\"" + RHeight + "\" id=\"" + id + "\" fill=\"url(#" + NodeType + ")\" stroke=\""
						+ stroke + "\" stroke-width=\"1.5\" ></rect>\n";
				if (NodeType.equals("XNode_rect") || NodeType.equals("YNode_rect")) {
					SvgStr = SvgStr + NodeText;
					ReturnXml = ReturnXml + "\n" + SvgStr;
				} else {
					ReturnXml = SvgStr;
				}
				EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else
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
		String Nodeid = "";
		String NodeNum = "";
		String LinkeyStartObj = "";
		String LinkeyEndObj = "";
		String ondblclick = "";

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
				Nodeid = GetVal(VmlStr, "Nodeid=\"", "\"");
				NodeNum = GetVal(VmlStr, "NodeNum=\"", "\"");
				LinkeyStartObj = GetVal(VmlStr, "LinkeyStartObj=\"", "\"");
				LinkeyEndObj = GetVal(VmlStr, "LinkeyEndObj=\"", "\"");
				ondblclick = GetVal(VmlStr, "ondblclick=\"", "\"");
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
					SvgStr = "<polygon id=\"Node" + NodeNum + "\"" + " Nodeid=\"" + Nodeid + "\"" + " NodeType=\""
							+ NodeType + "\"" + " ondblclick=\"" + ondblclick + "\"" + " NodeNum=\"" + NodeNum + "\""
							+ " LinkeyStartObj=\"" + LinkeyStartObj + "\"" + " LinkeyEndObj=\"" + LinkeyEndObj + "\""
							+ "points=\"" + (i + 30) + "," + (j - 25) + " " + (i + 80) + "," + (j + 5) + " " + (i + 30)
							+ "," + (j + 35) + " " + (i - 20) + "," + (j + 5)
							+ "\" stroke=\"#004d86\" stroke-width=\"1.5\" fill=\"url(#SNode)\"/>";

					SvgStr = SvgStr + "\n<text" + " id=\"Node" + NodeNum + "_text\"" + " Nodeid=\"" + Nodeid + "_text\""
							+ " NodeType=\"" + NodeType + "\" ondblclick=\"" + ondblclick + "\" x=\"" + (i + 30)
							+ "\" y=\"" + (j + 10) + "\"  text-anchor=\"middle\" font-size=\"11pt\" fill=\"black\" >"
							+ NodeText + "</text>\n";
				} else if (NodeType.equals("SubProcess")) {
					// NodeText = FormatNodeText(NodeText,,Long.toString(i +
					// 35), Long.toString(j));
					id = id.replace("S", "");
					SvgStr = "\n<path id=\"" + id + "\" transform=\"translate(" + (i - 23) + "," + (j - 25)
							+ ")\" d=\"M0 0 L96 0 L96 50 L86 50 L86 0 L86 50 L10 50 L10 0 L10 50 L0 50 Z\" "
							+ " Nodeid=\"" + Nodeid + "\"" + " NodeType=\"" + NodeType + "\"" + " ondblclick=\""
							+ ondblclick + "\"" + " NodeNum=\"" + NodeNum + "\"" + " LinkeyStartObj=\"" + LinkeyStartObj
							+ "\"" + " LinkeyEndObj=\"" + LinkeyEndObj + "\""
							+ " stroke=\"#004d86\" stroke-width=\"1.5\" fill=\"url(#SubProcess)\" ></path>\n";
					SvgStr = SvgStr + "\n<text" + " id=\"" + id + "_text\"" + " Nodeid=\"" + Nodeid + "_text\""
							+ " NodeType=\"" + NodeType + "\" ondblclick=\"" + ondblclick + "\" x=\"" + (i - 0 + 25)
							+ "\" y=\"" + (j + 5) + "\"  text-anchor=\"middle\" font-size=\"11pt\" fill=\"black\" >"
							+ NodeText + "</text>\n";
				} else if (NodeType.equals("OutProcess")) {
					id = id.replace("W", "");
					SvgStr = "\n<path id=\"" + id + "\" transform=\"translate(" + (i - 23) + "," + (j - 25)
							+ ")\" d=\"M10 0 L86 0 A10 10, 0, 0, 0,96 10 L96 40 A10 10, 0, 0, 0,86 50 L10 50 A10 10, 0, 0, 0,0 40 L0 10 A10 10, 0, 0, 0,10 0Z\"  "
							+ " Nodeid=\"" + Nodeid + "\"" + " NodeType=\"" + NodeType + "\"" + " ondblclick=\""
							+ ondblclick + "\"" + " NodeNum=\"" + NodeNum + "\"" + " LinkeyStartObj=\"" + LinkeyStartObj
							+ "\"" + " LinkeyEndObj=\"" + LinkeyEndObj + "\""
							+ "stroke=\"#004d86\" stroke-width=\"1.5\" fill=\"url(#OutProcess)\" ></path>\n";
					SvgStr = SvgStr + "\n<text" + " id=\"" + id + "_text\"" + " Nodeid=\"" + Nodeid + "_text\""
							+ " NodeType=\"" + NodeType + "\" ondblclick=\"" + ondblclick + "\" x=\"" + (i - 0 + 25)
							+ "\" y=\"" + (j + 5) + "\"  text-anchor=\"middle\" font-size=\"11pt\" fill=\"black\" >"
							+ NodeText + "</text>\n";
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
				if (GetId(VmlStr) == null) {
					//GetId(VmlStr);
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
					SvgStr = "<text x=\"" + (i) + "\" y=\"" + (j) + "\"  font-size=\"9pt\" "
							+ " stroke-width=\"1\" NodeType=\"spantext\""
							+ " fill=\"black\"  my-index=\"4\" text-algin=\"center\">" + NodeText + "</text>\n";

				}
				ReturnXml = ReturnXml + "\n" + SvgStr;
				EndStr = EndStr.substring(EndStr.indexOf(endcode) + endcode.length(), EndStr.length());
				rStr = EndStr;
				tempStr = tempStr + StartStr;
				startpos = rStr.indexOf(startcode);
			}
			tempStr = tempStr + rStr;
		} else
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
				} else
					tempStr = tempStr + StartStr + "</tspan><tspan x=\"" + x + "\" dy=\"20\">";
				rStr = rStr.substring(rStr.indexOf("<BR>") + 4, rStr.length());
				startpos = rStr.indexOf(startcode);
				max = max + 1;
			}
			tempStr = tempStr + rStr + "</tspan>";
		} else
			tempStr = SpanStr;
		ToSpanBr = tempStr;
		return ToSpanBr;
	}

	private static String FormatNodeText(String NodeText, String str, String x, String y) {
		String FormatNodeText = "";
		FormatNodeText = "<text " + str + " x=\"" + x + "\" y=\"" + y
				+ "\"  text-anchor=\"middle\" font-size=\"11pt\"  >" + NodeText + "</text>\n";
		return FormatNodeText;
	}

	public static String getSvgXml(String VmlStr) {
		String GetSvgXml = "";
		VmlStr = unSetCode(VmlStr);
		GoalVmlBody = VmlStr;
		GoalVmlBody = VmlStr;
		GetSvgXml = GetSvgXml + ToXYNode(GoalVmlBody);
		GetSvgXml = GetSvgXml + ToPolyline(GoalVmlBody);
		GetSvgXml = GetSvgXml + ToCircle(GoalVmlBody);
		GetSvgXml = GetSvgXml + ToActivity(GoalVmlBody);
		GetSvgXml = GetSvgXml + ToPolygon(GoalVmlBody);
		GetSvgXml = GetSvgXml + ToSpan(VmlStr).trim();
		SvgHead = ToSvgHead();
		GetSvgXml = SvgHead + (GoalAreaXml == null ? "" : GoalAreaXml) + GetSvgXml;
		return GetSvgXml;
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
		arr = unSetCode(arr);
		arr = Rdb.deCode(arr, false);
		System.out.println(getSvgXml(arr));

	}
}
