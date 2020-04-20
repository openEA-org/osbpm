function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u65b0\u589e\u5217 ";
    txtLang[1].innerHTML = "\u65b0\u589e\u6b04 ";
    txtLang[2].innerHTML = "\u589e\u52a0 /\u522a\u9664\u5217\u5408\u4f75 ";
    txtLang[3].innerHTML = "\u589e\u52a0 /\u522a\u9664\u6b04\u5408\u4f75 ";
    txtLang[4].innerHTML = "\u522a\u9664\u5217 ";
    txtLang[5].innerHTML = "\u522a\u9664\u6b04 ";

	document.getElementById("btnInsRowAbove").title="\u65b0\u589e\u4e0a\u65b9\u5217 ";
	document.getElementById("btnInsRowBelow").title="\u65b0\u589e\u4e0b\u65b9\u5217 ";
	document.getElementById("btnInsColLeft").title="\u65b0\u589e\u5de6\u65b9\u6b04 ";
	document.getElementById("btnInsColRight").title="\u65b0\u589e\u53f3\u65b9\u6b04 ";
	document.getElementById("btnIncRowSpan").title="\u589e\u52a0\u5217\u5408\u4f75 ";
	document.getElementById("btnDecRowSpan").title="\u522a\u9664\u5217\u5408\u4f75 ";
	document.getElementById("btnIncColSpan").title="\u589e\u52a0\u6b04\u5408\u4f75 ";
	document.getElementById("btnDecColSpan").title="\u522a\u9664\u6b04\u5408\u4f75 ";
	document.getElementById("btnDelRow").title="\u522a\u9664\u5217 ";
	document.getElementById("btnDelCol").title="\u522a\u9664\u6b04 ";
	document.getElementById("btnClose").value = " \u95dc\u9589  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "\u4e0d\u80fd\u522a\u9664\u6b04 .":
            return "\u4e0d\u80fd\u522a\u9664\u6b04\uff0c\u8acb\u5148\u6d88\u9664\u6b04\u5408\u4f75 .";
        case "\u4e0d\u80fd\u522a\u9664\u5217 .":
            return "\u4e0d\u80fd\u522a\u9664\u5217\uff0c\u8acb\u5148\u6d88\u9664\u5217\u5408\u4f75 .";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u8868\u683c\u5927\u5c0f </title>")
    }