function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    //txtLang[0].innerHTML = "\u5927\u5c0f ";
    txtLang[0].innerHTML = "\u81ea\u52d5\u8abf\u6574 ";
    txtLang[1].innerHTML = "\u5c6c\u6027 ";
    txtLang[2].innerHTML = "CSS\u6a23\u5f0f ";
    //txtLang[4].innerHTML = "\u63d2\u5165\u5217 ";
    //txtLang[5].innerHTML = "\u63d2\u5165\u6b04 ";
    //txtLang[6].innerHTML = "\u8de8 /\u5206\u62c6  \u5217 ";
    //txtLang[7].innerHTML = "\u8de8 /\u5206\u62c6  \u6b04 ";
    //txtLang[8].innerHTML = "\u522a\u9664\u5217 ";
    //txtLang[9].innerHTML = "\u522a\u9664\u6b04 ";    
    txtLang[3].innerHTML = "\u5bec\u5ea6 ";
    txtLang[4].innerHTML = "\u81ea\u52d5\u8abf\u6574\u6210\u5167\u5bb9\u5927\u5c0f ";
    txtLang[5].innerHTML = "\u56fa\u5b9a\u8868\u683c\u5bec\u5ea6 ";
    txtLang[6].innerHTML = "\u81ea\u52d5\u8abf\u6574\u6210\u8996\u7a97\u5927\u5c0f ";
    txtLang[7].innerHTML = "\u9ad8\u5ea6 ";
    txtLang[8].innerHTML = "\u81ea\u52d5\u8abf\u6574\u6210\u5167\u5bb9\u5927\u5c0f ";
    txtLang[9].innerHTML = "\u56fa\u5b9a\u8868\u683c\u9ad8\u5ea6 ";
    txtLang[10].innerHTML = "\u81ea\u52d5\u8abf\u6574\u6210\u8996\u7a97\u5927\u5c0f ";
    txtLang[11].innerHTML = "\u5c0d\u9f4a ";
    txtLang[12].innerHTML = "\u908a\u754c ";
    txtLang[13].innerHTML = "\u5de6 ";
    txtLang[14].innerHTML = "\u53f3 ";
    txtLang[15].innerHTML = "\u4e0a ";
    txtLang[16].innerHTML = "\u4e0b ";
    txtLang[17].innerHTML = "\u908a\u6846 ";
    txtLang[18].innerHTML = "\u647a\u758a ";
    txtLang[19].innerHTML = "\u80cc\u666f ";
    txtLang[20].innerHTML = "\u5132\u5b58\u683c\u9593\u8ddd ";
    txtLang[21].innerHTML = "\u5132\u5b58\u683c\u5167\u8ddd ";
    txtLang[22].innerHTML = "CSS\u6587\u5b57\u6a23\u5f0f ";
        
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u50cf\u7d20 "
    optLang[1].text = "\u767e\u4efd\u6bd4 "
    optLang[2].text = "\u50cf\u7d20 "
    optLang[3].text = "\u767e\u4efd\u6bd4 "
    optLang[4].text = "\u5de6 "
    optLang[5].text = "\u4e2d "
    optLang[6].text = "\u53f3 "
    optLang[7].text = "\u7121\u908a\u6846 "
    optLang[8].text = "\u662f "
    optLang[9].text = "\u5426 "

    document.getElementById("btnPick").value="\u8272\u5f69 ";
    document.getElementById("btnImage").value="\u5f71\u50cf ";

    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnApply").value = "\u61c9\u7528 ";
    document.getElementById("btnOk").value = " \u78ba\u8a8d  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Cannot delete column.":
            return "\u4e0d\u80fd\u522a\u9664\u6b04\uff0c\u8acb\u5148\u79fb\u9664\u8de8\u6b04\u3002 ";
        case "Cannot delete row.":
            return "\u4e0d\u80fd\u522a\u9664\u5217\uff0c\u8acb\u5148\u79fb\u9664\u8de8\u5217\u3002 ";
        case "Custom Colors": return "\u81ea\u8a02\u8272\u5f69 ";
        case "More Colors...": return "\u66f4\u591a\u8272\u5f69 ...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u8868\u683c\u5c6c\u6027 </title>")
    }