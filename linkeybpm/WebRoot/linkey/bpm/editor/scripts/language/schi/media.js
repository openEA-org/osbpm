function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u6863\u6848 ";
    txtLang[1].innerHTML = "\u5bbd\u5ea6 ";
    txtLang[2].innerHTML = "\u9ad8\u5ea6 ";    
    txtLang[3].innerHTML = "\u81ea\u52a8\u64ad\u653e ";    
    txtLang[4].innerHTML = "\u663e\u793a\u63a7\u5236\u53f0 ";
    txtLang[5].innerHTML = "\u663e\u793a\u72b6\u6001\u5217 ";   
    txtLang[6].innerHTML = "\u663e\u793a\u64ad\u653e\u53f0 ";
    txtLang[7].innerHTML = "\u81ea\u52a8\u56de\u5e26 ";   

    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u591a\u5a92\u4f53 </title>")
    }
