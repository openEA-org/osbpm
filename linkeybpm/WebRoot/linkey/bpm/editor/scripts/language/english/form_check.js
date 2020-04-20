function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "字段名";
    txtLang[1].innerHTML = "字段值";
    txtLang[2].innerHTML = "默认";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Checked"
    optLang[1].text = "Unchecked"
    
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = " 确定 ";
    }
function writeTitle()
    {
    document.write("<title>复选框</title>")
    }