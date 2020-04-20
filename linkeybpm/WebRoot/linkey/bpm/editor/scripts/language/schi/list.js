function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7f16\u53f7 ";
    txtLang[1].innerHTML = "\u9879\u76ee\u7b26\u53f7 ";
    txtLang[2].innerHTML = "\u8d77\u59cb\u503c ";
    txtLang[3].innerHTML = "\u5de6\u8fb9\u8fb9\u754c ";
    txtLang[4].innerHTML = "\u5f71\u50cf\u4f5c\u7b26\u53f7\u7684\u8fde\u7ed3 "
    txtLang[5].innerHTML = "\u5de6\u8fb9\u8fb9\u754c ";
    
    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u5e94\u7528 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";   
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "\u8bf7\u5148\u9009\u62e9\u6b3e\u5f0f\u3002 ";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u9879\u76ee\u7b26\u53f7\u53ca\u7f16\u7801\u683c\u5f0f </title>")
    }
