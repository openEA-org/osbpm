function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "类型";
    txtLang[1].innerHTML = "名称";
    txtLang[2].innerHTML = "值";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "按扭"
    optLang[1].text = "提交"
    optLang[2].text = "重设"
        
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = "确定";
    }
function writeTitle()
    {
    document.write("<title>按扭</title>")
    }