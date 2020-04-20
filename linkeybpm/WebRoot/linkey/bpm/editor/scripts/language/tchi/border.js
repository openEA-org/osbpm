function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "\u984f\u8272 ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
	{
	switch(s)
		{
		case "No Border": return "\u7121\u908a\u6846 ";
		case "Outside Border": return "\u5916\u908a\u6846\u7dda ";
		case "Left Border": return "\u5de6\u908a\u6846\u7dda ";
		case "Top Border": return "\u4e0a\u908a\u6846\u7dda ";
		case "Right Border": return "\u53f3\u908a\u6846\u7dda ";
		case "Bottom Border": return "\u4e0b\u908a\u6846\u7dda ";
		case "Pick": return "\u9078\u64c7 ";
		case "Custom Colors": return "\u81ea\u8a02\u984f\u8272 ";
		case "More Colors...": return "\u66f4\u591a\u984f\u8272 ...";
		default: return "";
		}
	}
function writeTitle()
	{
	document.write("<title>\u6846\u67b6 </title>")
	}