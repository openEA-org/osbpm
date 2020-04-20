function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u8fde\u7ed3 ";
    txtLang[1].innerHTML = "\u6807\u97f1 ";
    txtLang[2].innerHTML = "\u76ee\u6807 ";
    txtLang[3].innerHTML = "\u8fde\u7ed3\u6807\u97f1 ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Self"
    optLang[1].text = "Blank"
    optLang[2].text = "Parent"
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u8d85\u7ea7\u94fe\u63a5 </title>")
    }
