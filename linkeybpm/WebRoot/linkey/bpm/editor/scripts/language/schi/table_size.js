function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u65b0\u589e\u5217 ";
    txtLang[1].innerHTML = "\u65b0\u589e\u680f ";
    txtLang[2].innerHTML = "\u589e\u52a0 /\u5220\u9664\u5217\u5408\u5e76 ";
    txtLang[3].innerHTML = "\u589e\u52a0 /\u5220\u9664\u680f\u5408\u5e76 ";
    txtLang[4].innerHTML = "\u5220\u9664\u5217 ";
    txtLang[5].innerHTML = "\u5220\u9664\u680f ";

	document.getElementById("btnInsRowAbove").title="\u65b0\u589e\u4e0a\u65b9\u5217 ";
	document.getElementById("btnInsRowBelow").title="\u65b0\u589e\u4e0b\u65b9\u5217 ";
	document.getElementById("btnInsColLeft").title="\u65b0\u589e\u5de6\u65b9\u680f ";
	document.getElementById("btnInsColRight").title="\u65b0\u589e\u53f3\u65b9\u680f ";
	document.getElementById("btnIncRowSpan").title="\u589e\u52a0\u5217\u5408\u5e76 ";
	document.getElementById("btnDecRowSpan").title="\u5220\u9664\u5217\u5408\u5e76 ";
	document.getElementById("btnIncColSpan").title="\u589e\u52a0\u680f\u5408\u5e76 ";
	document.getElementById("btnDecColSpan").title="\u5220\u9664\u680f\u5408\u5e76 ";
	document.getElementById("btnDelRow").title="\u5220\u9664\u5217 ";
	document.getElementById("btnDelCol").title="\u5220\u9664\u680f ";
	document.getElementById("btnClose").value = " \u5173\u95ed  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "\u4e0d\u80fd\u5220\u9664\u680f .":
            return "\u4e0d\u80fd\u5220\u9664\u680f\uff0c\u8bf7\u5148\u6d88\u9664\u680f\u5408\u5e76 .";
        case "\u4e0d\u80fd\u5220\u9664\u5217 .":
            return "\u4e0d\u80fd\u5220\u9664\u5217\uff0c\u8bf7\u5148\u6d88\u9664\u5217\u5408\u5e76 .";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u8868\u683c\u5927\u5c0f </title>")
    }
