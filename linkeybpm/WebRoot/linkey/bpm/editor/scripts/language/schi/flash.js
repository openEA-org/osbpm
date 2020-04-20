function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u6863\u6848 ";
    txtLang[1].innerHTML = "\u80cc\u666f\u8272\u5f69 ";
    txtLang[2].innerHTML = "\u5bbd\u5ea6 ";
    txtLang[3].innerHTML = "\u9ad8\u5ea6 ";    
    txtLang[4].innerHTML = "\u54c1\u8d28 ";   
    txtLang[5].innerHTML = "\u5bf9\u9f50 ";
    txtLang[6].innerHTML = "\u91cd\u590d\u64ad\u653e ";
    txtLang[7].innerHTML = "\u662f ";
    txtLang[8].innerHTML = "\u5426 ";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsPage";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u4f4e "
    optLang[1].text = "\u9ad8 "
    optLang[2].text = "<\u6ca1\u6709\u8bbe\u5b9a >"
    optLang[3].text = "\u5de6 "
    optLang[4].text = "\u53f3 "
    optLang[5].text = "\u4e0a "
    optLang[6].text = "\u4e0b "
    
    document.getElementById("btnPick").value = "\u9009\u62e9 ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "\u81ea\u8ba2\u8272\u5f69 ";
        case "More Colors...": return "\u66f4\u591a\u8272\u5f69 ...";
        default: return "";
        }
    }    
function writeTitle()
    {
    document.write("<title>\u63d2\u5165  Flash</title>")
    }
