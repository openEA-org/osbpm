function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u6a94\u6848 ";
    txtLang[1].innerHTML = "\u5f71\u50cf\u66ff\u4ee3\u6587\u5b57 ";
    txtLang[2].innerHTML = "\u8ddd\u96e2 ";
    txtLang[3].innerHTML = "\u5c0d\u9f4a ";
    txtLang[4].innerHTML = "\u4e0a ";
    txtLang[5].innerHTML = "\u908a\u6846 ";
    txtLang[6].innerHTML = "\u4e0b ";
    txtLang[7].innerHTML = "\u5bec\u5ea6 ";
    txtLang[8].innerHTML = "\u5de6 ";
    txtLang[9].innerHTML = "\u9ad8\u5ea6 ";
    txtLang[10].innerHTML = "\u53f3 ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u7d55\u5c0d\u5411\u4e0b ";
    optLang[1].text = "\u7d55\u5c0d\u4e2d\u9593 ";
    optLang[2].text = "\u57fa\u6e96\u7dda ";
    optLang[3].text = "\u9760\u4e0b\u5c0d\u9f4a ";
    optLang[4].text = "\u5411\u5de6\u5c0d\u9f4a ";
    optLang[5].text = "\u7f6e\u4e2d\u5c0d\u9f4a ";
    optLang[6].text = "\u5411\u53f3\u5c0d\u9f4a ";
    optLang[7].text = "\u6587\u5b57\u4e0a\u65b9 ";
    optLang[8].text = "\u9760\u4e0a\u5c0d\u9f4a ";
 
    document.getElementById("btnBorder").value = " \u908a\u6846\u6a23\u5f0f  ";
    document.getElementById("btnReset").value = "\u91cd\u8a2d "
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u5f71\u50cf </title>")
    }