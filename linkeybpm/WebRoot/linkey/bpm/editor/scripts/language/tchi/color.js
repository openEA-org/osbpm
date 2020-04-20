function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u7db2\u9801\u8abf\u8272\u76e4 ";
    txtLang[1].innerHTML = "\u9078\u6709\u540d\u7a31\u7684\u984f\u8272 ";
    txtLang[2].innerHTML = "\u5b89\u5168\u7684\u7db2\u9801\u984f\u8272 ";
    txtLang[3].innerHTML = "\u65b0\u7684\u984f\u8272 ";
    txtLang[4].innerHTML = "\u73fe\u5728\u7684\u984f\u8272 ";
    txtLang[5].innerHTML = "\u81ea\u8a02\u984f\u8272 ";
    
    document.getElementById("btnAddToCustom").value = "\u65b0\u589e\u5230\u81ea\u8a02\u7684\u984f\u8272 ";
    document.getElementById("btnCancel").value = " \u53d6\u6d88  ";
    document.getElementById("btnRemove").value = " \u984f\u8272\u79fb\u9664  ";
    document.getElementById("btnApply").value = " \u61c9\u7528  ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function writeTitle()
    {
    document.write("<title>\u8272\u5f69 </title>")
    }
