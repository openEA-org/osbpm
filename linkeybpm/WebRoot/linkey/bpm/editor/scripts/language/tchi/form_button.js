function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7a2e\u985e ";
    txtLang[1].innerHTML = "\u540d\u7a31 ";
    txtLang[2].innerHTML = "\u503c ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6309\u9215 "
    optLang[1].text = "\u78ba\u8a8d\u5132\u5b58 "
    optLang[2].text = "\u91cd\u8a2d "
        
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u6309\u9215 </title>")
    }