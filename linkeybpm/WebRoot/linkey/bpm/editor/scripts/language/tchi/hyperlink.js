function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u9023\u7d50 ";
    txtLang[1].innerHTML = "\u6a19\u97f1 ";
    txtLang[2].innerHTML = "\u76ee\u6a19 ";
    txtLang[3].innerHTML = "\u9023\u7d50\u6a19\u97f1 ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Self"
    optLang[1].text = "Blank"
    optLang[2].text = "Parent"
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u8d85\u9023\u7d50 </title>")
    }