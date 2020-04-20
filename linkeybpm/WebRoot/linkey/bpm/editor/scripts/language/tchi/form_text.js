function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7a2e\u985e ";
    txtLang[1].innerHTML = "\u540d\u7a31 ";
    txtLang[2].innerHTML = "\u5927\u5c0f ";
    txtLang[3].innerHTML = "\u5b57\u5143\u9577\u5ea6\u4e0a\u9650 ";
    txtLang[4].innerHTML = "\u884c\u6578 ";
    txtLang[5].innerHTML = "\u503c ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6587\u5b57\u6b04\u4f4d "
    optLang[1].text = "\u6587\u5b57\u5340\u57df "
    optLang[2].text = "\u5bc6\u78bc\u6b04\u4f4d "
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u6587\u5b57\u6b04\u4f4d </title>")
    }