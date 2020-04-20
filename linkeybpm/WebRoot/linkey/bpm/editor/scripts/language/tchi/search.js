function loadTxt()
	{
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5c0b\u627e ";
    txtLang[1].innerHTML = "\u53d6\u4ee3 ";
    txtLang[2].innerHTML = "\u5927\u5c0f\u5beb\u9808\u76f8\u7b26 ";
    txtLang[3].innerHTML = "\u5168\u5b57\u62fc\u5beb\u9808\u76f8\u7b26 ";
    
    document.getElementById("btnSearch").value = "\u5c0b\u627e\u4e0b\u4e00\u500b ";;
    document.getElementById("btnReplace").value = "\u53d6\u4ee3 ";
    document.getElementById("btnReplaceAll").value = "\u4e9b\u90e8\u53d6\u4ee3 ";  
    document.getElementById("btnClose").value = "\u95dc\u9589 ";
	}
function getTxt(s)
    {
    switch(s)
        {
        case "Finished searching": return "Finished searching the document.\nSearch again from the top?";
        default: return "";
        }
    }
function writeTitle()
	{
	document.write("<title>\u5c0b\u627e\u548c\u53d6\u4ee3 </title>")
	}