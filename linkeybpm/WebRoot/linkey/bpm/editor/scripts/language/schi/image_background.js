function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5f71\u50cf\u6863\u6848 ";
    txtLang[1].innerHTML = "\u91cd\u590d ";
    txtLang[2].innerHTML = "\u6c34\u5e73\u5bf9\u9f50 ";
    txtLang[3].innerHTML = "\u5782\u76f4\u5bf9\u9f50 ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u91cd\u590d "
    optLang[1].text = "\u4e0d\u91cd\u590d "
    optLang[2].text = "\u6a2a\u5411\u91cd\u590d "
    optLang[3].text = "\u5782\u76f4\u91cd\u590d "
    optLang[4].text = "\u5de6 "
    optLang[5].text = "\u4e2d "
    optLang[6].text = "\u53f3 "
    optLang[7].text = "\u50cf\u7d20 "
    optLang[8].text = "\u767e\u4efd\u6bd4 "
    optLang[9].text = "\u4e0a "
    optLang[10].text = "\u4e2d "
    optLang[11].text = "\u4e0b "
    optLang[12].text = "\u50cf\u7d20 "
    optLang[13].text = "\u767e\u4efd\u6bd4 "
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u80cc\u666f\u5f71\u50cf </title>")
    }
