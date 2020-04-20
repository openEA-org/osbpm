function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "字段名";
    txtLang[1].innerHTML = "字段值";
    
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = " 确定 ";
    }
function writeTitle()
    {
    document.write("<title>隐藏字段</title>")
    }
