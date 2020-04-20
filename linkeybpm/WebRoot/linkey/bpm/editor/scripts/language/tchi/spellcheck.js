function loadTxt()
	{
	document.getElementById("btnCheckAgain").value = " Check Again ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
	}
function getTxt(s)
	{
	switch(s)
		{
		case "Required":
			return "\u9808\u8981 ieSpell\u7a0b\u5f0f  (from www.iespell.com).";
		default:return "";
		}
	}
function writeTitle()
	{
	document.write("<title>\u62fc\u5b57\u6aa2\u67e5 : \u82f1\u6587 </title>")
	}