function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "图片路径";
    txtLang[1].innerHTML = "标题";
    txtLang[2].innerHTML = "间距";
    txtLang[3].innerHTML = "位置";
    txtLang[4].innerHTML = "上";
    txtLang[5].innerHTML = "边框";
    txtLang[6].innerHTML = "下";
    txtLang[7].innerHTML = "宽度";
    txtLang[8].innerHTML = "左";
    txtLang[9].innerHTML = "高度";
    txtLang[10].innerHTML = "右";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "absBottom";
    optLang[1].text = "absMiddle";
    optLang[2].text = "baseline";
    optLang[3].text = "bottom";
    optLang[4].text = "left";
    optLang[5].text = "middle";
    optLang[6].text = "right";
    optLang[7].text = "textTop";
    optLang[8].text = "top";
 
    document.getElementById("btnBorder").value = " 选择样式 ";
    document.getElementById("btnReset").value = "复原"
    
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnInsert").value = "插入";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = "确定";
    }
function writeTitle()
    {
    document.write("<title>Image</title>")
    }