function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "\u989c\u8272 ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
	{
	switch(s)
		{
		case "No Border": return "\u65e0\u8fb9\u6846 ";
		case "Outside Border": return "\u5916\u8fb9\u6846\u7ebf ";
		case "Left Border": return "\u5de6\u8fb9\u6846\u7ebf ";
		case "Top Border": return "\u4e0a\u8fb9\u6846\u7ebf ";
		case "Right Border": return "\u53f3\u8fb9\u6846\u7ebf ";
		case "Bottom Border": return "\u4e0b\u8fb9\u6846\u7ebf ";
		case "Pick": return "\u9009\u62e9 ";
		case "Custom Colors": return "\u81ea\u8ba2\u989c\u8272 ";
		case "More Colors...": return "\u66f4\u591a\u989c\u8272 ...";
		default: return "";
		}
	}
function writeTitle()
	{
	document.write("<title>\u6846\u67b6 </title>")
	}
