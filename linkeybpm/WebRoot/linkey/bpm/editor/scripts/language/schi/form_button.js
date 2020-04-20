function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u79cd\u7c7b ";
    txtLang[1].innerHTML = "\u540d\u79f0 ";
    txtLang[2].innerHTML = "\u503c ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6309\u94ae "
    optLang[1].text = "\u786e\u8ba4\u50a8\u5b58 "
    optLang[2].text = "\u91cd\u8bbe "
        
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u6309\u94ae </title>")
    }
