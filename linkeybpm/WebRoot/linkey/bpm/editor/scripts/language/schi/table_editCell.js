function loadTxt()
    {
    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u81ea\u52a8\u8c03\u6574 ";
    txtLang[1].innerHTML = "\u5c5e\u6027 ";
    txtLang[2].innerHTML = "CSS\u6837\u5f0f ";
    txtLang[3].innerHTML = "\u5bbd\u5ea6 ";
    txtLang[4].innerHTML = "\u81ea\u52a8\u8c03\u6574\u6210\u5185\u5bb9\u5927\u5c0f ";
    txtLang[5].innerHTML = "\u56fa\u5b9a\u50a8\u5b58\u683c\u5bbd\u5ea6 ";
    txtLang[6].innerHTML = "\u9ad8\u5ea6 ";
    txtLang[7].innerHTML = "\u81ea\u52a8\u8c03\u6574\u6210\u5185\u5bb9\u5927\u5c0f ";
    txtLang[8].innerHTML = "\u56fa\u5b9a\u50a8\u5b58\u683c\u9ad8\u5ea6 ";
    txtLang[9].innerHTML = "\u6587\u5b57\u5bf9\u9f50 ";
    txtLang[10].innerHTML = "\u5185\u8ddd ";
    txtLang[11].innerHTML = "\u5de6 ";
    txtLang[12].innerHTML = "\u53f3 ";
    txtLang[13].innerHTML = "\u4e0a ";
    txtLang[14].innerHTML = "\u4e0b ";
    txtLang[15].innerHTML = "\u767d\u683c ";
    txtLang[16].innerHTML = "\u80cc\u666f ";
    txtLang[17].innerHTML = "\u9884\u89c8 ";
    txtLang[18].innerHTML = "CSS\u6837\u5f0f ";
    txtLang[19].innerHTML = "\u5e94\u7528\u81f3 ";

    document.getElementById("btnPick").value = "\u8272\u5f69 ";
    document.getElementById("btnImage").value = "\u5f71\u50cf ";
    document.getElementById("btnText").value = " \u6587\u5b57\u683c\u5f0f  ";
    document.getElementById("btnBorder").value = " \u8fb9\u6846\u6837\u8272  ";

    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u50cf\u7d20 "
    optLang[1].text = "\u767e\u4efd\u6bd4 "
    optLang[2].text = "\u50cf\u7d20 "
    optLang[3].text = "\u767e\u4efd\u6bd4 "
    optLang[4].text = "\u65e0\u8bbe\u5b9a "
    optLang[5].text = "\u5411\u4e0a\u5bf9\u9f50 "
    optLang[6].text = "\u7f6e\u4e2d\u5bf9\u9f50 "
    optLang[7].text = "\u5411\u4e0b\u5bf9\u9f50 "
    optLang[8].text = "\u57fa\u51c6\u7ebf "
    optLang[9].text = "sub"
    optLang[10].text = "super"
    optLang[11].text = "text-top"
    optLang[12].text = "text-bottom"
    optLang[13].text = "not set"
    optLang[14].text = "left"
    optLang[15].text = "center"
    optLang[16].text = "right"
    optLang[17].text = "justify"
    optLang[18].text = "Not Set"
    optLang[19].text = "No Wrap"
    optLang[20].text = "pre"
    optLang[21].text = "Normal"
    optLang[22].text = "Current Cell"
    optLang[23].text = "Current Row"
    optLang[24].text = "Current Column"
    optLang[25].text = "Whole Table"
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
    document.write("<title>Cell Properties</title>")
    }
