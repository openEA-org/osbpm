function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5bf9\u9f50 ";
    txtLang[1].innerHTML = "\u7f29\u6392 ";
    txtLang[2].innerHTML = "\u5b57\u95f4\u8ddd\u79bb ";
    txtLang[3].innerHTML = "\u5b57\u7b26\u8ddd\u79bb ";
    txtLang[4].innerHTML = "\u884c\u9ad8 ";
    txtLang[5].innerHTML = "\u5b57\u4f53\u5927\u5c0f\u5199 ";
    txtLang[6].innerHTML = "\u767d\u7a7a\u683c ";
    
    document.getElementById("divPreview").innerHTML = "this para try to show you the effect, " +
        "\u663e\u793a\u6548\u679c , " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6ca1\u6709\u8bbe\u5b9a ";
    optLang[1].text = "\u5de6 ";
    optLang[2].text = "\u53f3 ";
    optLang[3].text = "\u4e2d ";
    optLang[4].text = "\u5de6\u53f3\u5bf9\u9f50 ";
    optLang[5].text = "\u6ca1\u6709\u8bbe\u5b9a ";
    optLang[6].text = "\u6807\u9898\u5927\u5199 ";
    optLang[7].text = "\u5927\u5199 ";
    optLang[8].text = "\u5c0f\u5199 ";
    optLang[9].text = "\u6ca1\u6709 ";
    optLang[10].text = "\u6ca1\u6709\u8bbe\u5b9a ";
    optLang[11].text = "\u4e0d\u6362\u884c ";
    optLang[12].text = "pre";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";   
    }
function writeTitle()
    {
    document.write("<title>\u6bb5\u843d\u683c\u5f0f </title>")
    }
