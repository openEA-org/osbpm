function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u540d\u7a31 ";
    txtLang[1].innerHTML = "\u5927\u5c0f ";
    txtLang[2].innerHTML = "\u5141\u8a31\u591a\u9805\u9078\u64c7 ";
    txtLang[3].innerHTML = "\u503c ";
    
    document.getElementById("btnAdd").value = "  \u65b0\u589e   ";
    document.getElementById("btnUp").value = "  \u4e0a\u79fb   ";
    document.getElementById("btnDown").value = "  \u4e0b\u79fb   ";
    document.getElementById("btnDel").value = "  \u522a\u9664   ";
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u6e05\u55ae </title>")
    }