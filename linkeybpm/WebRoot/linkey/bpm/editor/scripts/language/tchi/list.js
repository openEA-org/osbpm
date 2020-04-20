function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7de8\u865f ";
    txtLang[1].innerHTML = "\u9805\u76ee\u7b26\u865f ";
    txtLang[2].innerHTML = "\u8d77\u59cb\u503c ";
    txtLang[3].innerHTML = "\u5de6\u908a\u908a\u754c ";
    txtLang[4].innerHTML = "\u5f71\u50cf\u4f5c\u7b26\u865f\u7684\u9023\u7d50 "
    txtLang[5].innerHTML = "\u5de6\u908a\u908a\u754c ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";   
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "\u8acb\u5148\u9078\u64c7\u6b3e\u5f0f\u3002 ";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u9805\u76ee\u7b26\u865f\u53ca\u7de8\u78bc\u683c\u5f0f </title>")
    }
