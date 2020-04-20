function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u79cd\u7c7b ";
    txtLang[1].innerHTML = "\u540d\u79f0 ";
    txtLang[2].innerHTML = "\u5927\u5c0f ";
    txtLang[3].innerHTML = "\u5b57\u7b26\u957f\u5ea6\u4e0a\u9650 ";
    txtLang[4].innerHTML = "\u884c\u6570 ";
    txtLang[5].innerHTML = "\u503c ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6587\u5b57\u5b57\u6bb5 "
    optLang[1].text = "\u6587\u5b57\u533a\u57df "
    optLang[2].text = "\u5bc6\u7801\u5b57\u6bb5 "
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u6587\u5b57\u5b57\u6bb5 </title>")
    }
