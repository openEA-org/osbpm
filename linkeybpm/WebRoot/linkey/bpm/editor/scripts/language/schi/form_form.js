function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u540d\u79f0 ";
    txtLang[1].innerHTML = "\u52a8\u4f5c ";
    txtLang[2].innerHTML = "\u65b9\u6cd5 ";
        
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u7a97\u4f53 </title>")
    }
