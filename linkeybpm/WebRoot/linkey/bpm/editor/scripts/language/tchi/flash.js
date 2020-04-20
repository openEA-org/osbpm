function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u6a94\u6848 ";
    txtLang[1].innerHTML = "\u80cc\u666f\u8272\u5f69 ";
    txtLang[2].innerHTML = "\u5bec\u5ea6 ";
    txtLang[3].innerHTML = "\u9ad8\u5ea6 ";    
    txtLang[4].innerHTML = "\u54c1\u8cea ";   
    txtLang[5].innerHTML = "\u5c0d\u9f4a ";
    txtLang[6].innerHTML = "\u91cd\u8907\u64ad\u653e ";
    txtLang[7].innerHTML = "\u662f ";
    txtLang[8].innerHTML = "\u5426 ";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "PluginsPage";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u4f4e "
    optLang[1].text = "\u9ad8 "
    optLang[2].text = "<\u6c92\u6709\u8a2d\u5b9a >"
    optLang[3].text = "\u5de6 "
    optLang[4].text = "\u53f3 "
    optLang[5].text = "\u4e0a "
    optLang[6].text = "\u4e0b "
    
    document.getElementById("btnPick").value = "\u9078\u64c7 ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "\u81ea\u8a02\u8272\u5f69 ";
        case "More Colors...": return "\u66f4\u591a\u8272\u5f69 ...";
        default: return "";
        }
    }    
function writeTitle()
    {
    document.write("<title>\u63d2\u5165  Flash</title>")
    }