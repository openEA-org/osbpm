function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u985e\u5225\u540d\u7a31 ";
    txtLang[1].innerHTML = "\u9810\u89bd ";
    txtLang[2].innerHTML = "\u6587\u5b57\u6a23\u5f0f ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "\u60a8\u5df2\u9078\u64c7\u4e86 BODY\u5143\u7d20 .";
        case "Please select a text.":
            return "\u8acb\u5148\u9078\u64c7\u6587\u5b57 .";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u81ea\u8a02\u6a23\u5f0f </title>")
    }