function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "\u63db\u884c ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "\u5c0b\u627e ";
        case "Cut":return "\u526a\u4e0b ";
        case "Copy":return "\u8907\u88fd ";
        case "Paste":return "\u8cbc\u4e0a ";
        case "Undo":return "\u5fa9\u539f ";
        case "Redo":return "\u53d6\u6d88\u5fa9\u539f ";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>HTML\u6e90\u78bc\u7de8\u8f2f\u5668 </title>")
    }
