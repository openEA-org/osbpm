function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u540d\u79f0 ";
    txtLang[1].innerHTML = "\u503c ";
    txtLang[2].innerHTML = "\u9884\u8bbe ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u5df1\u6838\u53d6 "
    optLang[1].text = "\u5df1\u6838\u53d6 "
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnInsert").value = "\u63d2\u5165 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>Radio Button</title>")
    }
