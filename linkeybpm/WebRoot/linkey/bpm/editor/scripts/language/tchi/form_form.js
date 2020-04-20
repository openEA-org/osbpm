function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u540d\u7a31 ";
    txtLang[1].innerHTML = "\u52d5\u4f5c ";
    txtLang[2].innerHTML = "\u65b9\u6cd5 ";
        
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u8868\u55ae </title>")
    }