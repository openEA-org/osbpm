function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "字段名";
    txtLang[1].innerHTML = "大小";
    txtLang[2].innerHTML = "允许多选";
    txtLang[3].innerHTML = "列表值VALUE----------列表名TEXT";
    
    document.getElementById("btnAdd").value = "  添加  ";
    document.getElementById("btnUp").value = "  上移  ";
    document.getElementById("btnDown").value = "  下移  ";
    document.getElementById("btnDel").value = "  删除  ";
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = " 确定 ";
    }
function writeTitle()
    {
    document.write("<title>下拉框</title>")
    }