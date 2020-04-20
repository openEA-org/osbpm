function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7c7b\u522b\u540d\u79f0 ";
    txtLang[1].innerHTML = "\u9884\u89c8 ";
    txtLang[2].innerHTML = "\u6587\u5b57\u6837\u5f0f ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "\u60a8\u5df2\u9009\u62e9\u4e86 BODY\u5143\u7d20 .";
        case "Please select a text.":
            return "\u8bf7\u5148\u9009\u62e9\u6587\u5b57 .";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u81ea\u8ba2\u6837\u5f0f </title>")
    }
