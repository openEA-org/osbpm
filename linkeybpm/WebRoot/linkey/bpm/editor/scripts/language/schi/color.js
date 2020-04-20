function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7f51\u9875\u8c03\u8272\u76d8 ";
    txtLang[1].innerHTML = "\u9009\u6709\u540d\u79f0\u7684\u989c\u8272 ";
    txtLang[2].innerHTML = "\u5b89\u5168\u7684\u7f51\u9875\u989c\u8272 ";
    txtLang[3].innerHTML = "\u65b0\u7684\u989c\u8272 ";
    txtLang[4].innerHTML = "\u73b0\u5728\u7684\u989c\u8272 ";
    txtLang[5].innerHTML = "\u81ea\u8ba2\u989c\u8272 ";
    
    document.getElementById("btnAddToCustom").value = "\u65b0\u589e\u5230\u81ea\u8ba2\u7684\u989c\u8272 ";
    document.getElementById("btnCancel").value = " \u53d6\u6d88  ";
    document.getElementById("btnRemove").value = " \u989c\u8272\u79fb\u9664  ";
    document.getElementById("btnApply").value = " \u5e94\u7528  ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function writeTitle()
    {
    document.write("<title>\u8272\u5f69 </title>")
    }
