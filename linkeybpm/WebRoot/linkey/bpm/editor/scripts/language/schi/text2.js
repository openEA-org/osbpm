var sStyleWeight1;
var sStyleWeight2;
var sStyleWeight3;
var sStyleWeight4; 

function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "\u5b57\u4f53 ";
    txtLang[1].innerHTML = "\u6837\u5f0f ";
    txtLang[2].innerHTML = "\u5927\u5c0f ";
    txtLang[3].innerHTML = "\u524d\u666f\u8272\u5f69 ";
    txtLang[4].innerHTML = "\u80cc\u666f\u8272\u5f69 ";
    
    txtLang[5].innerHTML = "\u6587\u5b57\u88c5\u9970 ";
    txtLang[6].innerHTML = "\u5927\u5c0f\u5199 ";
    txtLang[7].innerHTML = "\u8ff7\u4f60\u5c0f\u5199 ";
    txtLang[8].innerHTML = "\u5782\u76f4 ";

    txtLang[9].innerHTML = "\u6ca1\u6709\u8bbe\u5b9a ";
    txtLang[10].innerHTML = "\u5e95\u7ebf ";
    txtLang[11].innerHTML = "\u4e0a\u52a0\u7ebf ";
    txtLang[12].innerHTML = "\u5220\u9664\u7ebf ";
    txtLang[13].innerHTML = "\u6ca1\u6709 ";

    txtLang[14].innerHTML = "\u6ca1\u6709\u8bbe\u5b9a ";
    txtLang[15].innerHTML = "\u6807\u9898\u5927\u5199 ";
    txtLang[16].innerHTML = "\u5168\u90e8\u5927\u5199 ";
    txtLang[17].innerHTML = "\u5168\u90e8\u5c0f\u5199 ";
    txtLang[18].innerHTML = "\u6ca1\u6709 ";

    txtLang[19].innerHTML = "\u6ca1\u6709\u8bbe\u5b9a ";
    txtLang[20].innerHTML = "\u5927\u5199 ";
    txtLang[21].innerHTML = "\u666e\u901a ";
    txtLang[22].innerHTML = "\u6ca1\u6709\u8bbe\u5b9a ";
    txtLang[23].innerHTML = "\u4e0a\u6807 ";
    txtLang[24].innerHTML = "\u4e0b\u6807 ";
    txtLang[25].innerHTML = "\u76f8\u5bf9 ";
    txtLang[26].innerHTML = "\u57fa\u51c6\u7ebf ";
    
    txtLang[27].innerHTML = "\u5b57\u7b26\u95f4\u8ddd ";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "\u6807\u51c6 "
    optLang[1].text = "\u659c\u4f53 "
    optLang[2].text = "\u7c97\u4f53 "
    optLang[3].text = "\u7c97\u4f53\u52a0\u659c\u4f53 "
    
    optLang[0].value = "Regular"
    optLang[1].value = "Italic"
    optLang[2].value = "Bold"
    optLang[3].value = "Bold Italic"
    
    sStyleWeight1 = "Regular"
    sStyleWeight2 = "Italic"
    sStyleWeight3 = "Bold"
    sStyleWeight4 = "Bold Italic"
    
    optLang[4].text = "\u4e0a "
    optLang[5].text = "\u4e2d "
    optLang[6].text = "\u4e0b "
    optLang[7].text = "\u6587\u5b57\u4e0a\u65b9 "
    optLang[8].text = "\u6587\u5b57\u4e0b\u65b9 "
    
    document.getElementById("btnPick1").value = "\u8272\u5f69 ";
    document.getElementById("btnPick2").value = "\u8272\u5f69 ";

    document.getElementById("btnCancel").value = "\u53d6\u6d88 ";
    document.getElementById("btnOk").value = " \u786e\u8ba4  ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "\u81ea\u8ba2\u8272\u5f69 ";
        case "More Colors...": return "\u66f4\u591a\u8272\u5f69 ...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>\u6587\u5b57\u683c\u5f0f </title>")
    }
