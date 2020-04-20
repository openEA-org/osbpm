function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "类型";
    txtLang[1].innerHTML = "字段名";
    txtLang[2].innerHTML = "宽度";
    txtLang[3].innerHTML = "最大长度";
    txtLang[4].innerHTML = "TextArea宽度";
    txtLang[5].innerHTML = "默认值";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Text"
    optLang[1].text = "Textarea"
    optLang[2].text = "Password"
    
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = " 确定 ";
    }
function writeTitle()
    {
    document.write("<title>文本字段</title>")
    }