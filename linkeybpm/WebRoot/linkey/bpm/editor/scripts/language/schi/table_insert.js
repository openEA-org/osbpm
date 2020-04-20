function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5217 ";
    txtLang[1].innerHTML = "\u95f4\u8ddd ";
    txtLang[2].innerHTML = "\u680f ";
    txtLang[3].innerHTML = "\u5185\u8ddd ";
    txtLang[4].innerHTML = "\u8fb9\u6846 ";
    txtLang[5].innerHTML = "\u6298\u8fed ";
    
	var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u65e0\u8fb9\u6846 ";
    optLang[1].text = "\u662f ";
    optLang[2].text = "\u5426 ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";

    document.getElementById("btnSpan1").value = "\u5782\u76f4\u8de8\u680f ";
    document.getElementById("btnSpan2").value = "\u6c34\u5e73\u8de8\u680f ";
    }
function writeTitle()
    {
    document.write("<title>\u63d2\u5165\u8868\u683c </title>")
    }
