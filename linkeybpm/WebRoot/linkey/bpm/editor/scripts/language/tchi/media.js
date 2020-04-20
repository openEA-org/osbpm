function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u6a94\u6848 ";
    txtLang[1].innerHTML = "\u5bec\u5ea6 ";
    txtLang[2].innerHTML = "\u9ad8\u5ea6 ";    
    txtLang[3].innerHTML = "\u81ea\u52d5\u64ad\u653e ";    
    txtLang[4].innerHTML = "\u986f\u793a\u63a7\u5236\u81fa ";
    txtLang[5].innerHTML = "\u986f\u793a\u72c0\u614b\u5217 ";   
    txtLang[6].innerHTML = "\u986f\u793a\u64ad\u653e\u81fa ";
    txtLang[7].innerHTML = "\u81ea\u52d5\u56de\u5e36 ";   

    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u591a\u5a92\u9ad4 </title>")
    }
