function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Wrap Text";
    document.getElementById("btnCancel").value = "取消";
    document.getElementById("btnApply").value = "应用";
    document.getElementById("btnOk").value = " 确定 ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "搜索";
        case "Cut":return "剪切";
        case "Copy":return "拷贝";
        case "Paste":return "粘贴";
        case "Undo":return "后退";
        case "Redo":return "重重";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>编辑HTML源码</title>")
    }
