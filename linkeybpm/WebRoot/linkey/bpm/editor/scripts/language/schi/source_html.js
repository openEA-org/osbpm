function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "\u6362\u884c ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "\u5bfb\u627e ";
        case "Cut":return "\u526a\u4e0b ";
        case "Copy":return "\u590d\u5236 ";
        case "Paste":return "\u8d34\u4e0a ";
        case "Undo":return "\u590d\u539f ";
        case "Redo":return "\u53d6\u6d88\u590d\u539f ";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>HTML\u6e90\u7801\u7f16\u8f91\u5668 </title>")
    }
