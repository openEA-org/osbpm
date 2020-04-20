function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u989c\u8272 ";
    txtLang[1].innerHTML = "\u9634\u5f71 ";   
    
    txtLang[2].innerHTML = "\u8fb9\u754c ";
    txtLang[3].innerHTML = "\u5de6 ";
    txtLang[4].innerHTML = "\u53f3 ";
    txtLang[5].innerHTML = "\u4e0a ";
    txtLang[6].innerHTML = "\u4e0b ";
    
    txtLang[7].innerHTML = "\u50a8\u5b58\u683c\u5185\u8ddd ";
    txtLang[8].innerHTML = "\u5de6 ";
    txtLang[9].innerHTML = "\u53f3 ";
    txtLang[10].innerHTML = "\u4e0a ";
    txtLang[11].innerHTML = "\u4e0b ";
    
    txtLang[12].innerHTML = "\u91cf\u5ea6 ";
    txtLang[13].innerHTML = "\u9614\u5ea6 ";
    txtLang[14].innerHTML = "\u9ad8\u5ea6 ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u50cf\u7d20 ";
    optLang[1].text = "\u767e\u5206\u6bd4 ";
    optLang[2].text = "\u50cf\u7d20 ";
    optLang[3].text = "\u767e\u5206\u6bd4 ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "\u65e0\u8fb9\u6846 ";
        case "Outside Border": return "\u5916\u8fb9\u6846\u7ebf ";
        case "Left Border": return "\u5de6\u8fb9\u6846\u7ebf ";
        case "Top Border": return "\u4e0a\u8fb9\u6846\u7ebf ";
        case "Right Border": return "\u53f3\u8fb9\u6846\u7ebf ";
        case "Bottom Border": return "\u4e0b\u8fb9\u6846\u7ebf ";
        case "Pick": return "\u9009\u62e9 ";
        case "Custom Colors": return "\u81ea\u8ba2\u989c\u8272 ";
        case "More Colors...": return "\u66f4\u591a\u989c\u8272 ......";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u50a8\u5b58\u683c\u683c\u5f0f </title>")
    }
