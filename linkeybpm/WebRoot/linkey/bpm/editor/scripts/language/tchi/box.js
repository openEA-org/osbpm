function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u984f\u8272 ";
    txtLang[1].innerHTML = "\u9670\u5f71 ";   
    
    txtLang[2].innerHTML = "\u908a\u754c ";
    txtLang[3].innerHTML = "\u5de6 ";
    txtLang[4].innerHTML = "\u53f3 ";
    txtLang[5].innerHTML = "\u4e0a ";
    txtLang[6].innerHTML = "\u4e0b ";
    
    txtLang[7].innerHTML = "\u5132\u5b58\u683c\u5167\u8ddd ";
    txtLang[8].innerHTML = "\u5de6 ";
    txtLang[9].innerHTML = "\u53f3 ";
    txtLang[10].innerHTML = "\u4e0a ";
    txtLang[11].innerHTML = "\u4e0b ";
    
    txtLang[12].innerHTML = "\u91cf\u5ea6 ";
    txtLang[13].innerHTML = "\u6ff6\u5ea6 ";
    txtLang[14].innerHTML = "\u9ad8\u5ea6 ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u50cf\u7d20 ";
    optLang[1].text = "\u767e\u5206\u6bd4 ";
    optLang[2].text = "\u50cf\u7d20 ";
    optLang[3].text = "\u767e\u5206\u6bd4 ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "\u7121\u908a\u6846 ";
        case "Outside Border": return "\u5916\u908a\u6846\u7dda ";
        case "Left Border": return "\u5de6\u908a\u6846\u7dda ";
        case "Top Border": return "\u4e0a\u908a\u6846\u7dda ";
        case "Right Border": return "\u53f3\u908a\u6846\u7dda ";
        case "Bottom Border": return "\u4e0b\u908a\u6846\u7dda ";
        case "Pick": return "\u9078\u64c7 ";
        case "Custom Colors": return "\u81ea\u8a02\u984f\u8272 ";
        case "More Colors...": return "\u66f4\u591a\u984f\u8272 ......";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u5132\u5b58\u683c\u683c\u5f0f </title>")
    }