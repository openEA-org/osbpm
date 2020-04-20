function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5c0d\u9f4a ";
    txtLang[1].innerHTML = "\u7e2e\u6392 ";
    txtLang[2].innerHTML = "\u5b57\u9593\u8ddd\u96e2 ";
    txtLang[3].innerHTML = "\u5b57\u5143\u8ddd\u96e2 ";
    txtLang[4].innerHTML = "\u884c\u9ad8 ";
    txtLang[5].innerHTML = "\u5b57\u9ad4\u5927\u5c0f\u5beb ";
    txtLang[6].innerHTML = "\u767d\u7a7a\u683c ";
    
    document.getElementById("divPreview").innerHTML = "this para try to show you the effect, " +
        "\u986f\u793a\u6548\u679c , " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6c92\u6709\u8a2d\u5b9a ";
    optLang[1].text = "\u5de6 ";
    optLang[2].text = "\u53f3 ";
    optLang[3].text = "\u4e2d ";
    optLang[4].text = "\u5de6\u53f3\u5c0d\u9f4a ";
    optLang[5].text = "\u6c92\u6709\u8a2d\u5b9a ";
    optLang[6].text = "\u6a19\u984c\u5927\u5beb ";
    optLang[7].text = "\u5927\u5beb ";
    optLang[8].text = "\u5c0f\u5beb ";
    optLang[9].text = "\u6c92\u6709 ";
    optLang[10].text = "\u6c92\u6709\u8a2d\u5b9a ";
    optLang[11].text = "\u4e0d\u63db\u884c ";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";   
    }
function writeTitle()
    {
    document.write("<title>\u6bb5\u843d\u683c\u5f0f </title>")
    }
