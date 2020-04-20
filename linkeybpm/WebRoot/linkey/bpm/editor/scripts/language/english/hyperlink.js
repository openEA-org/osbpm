function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Bookmark";
    txtLang[2].innerHTML = "Target";
    txtLang[3].innerHTML = "Title";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Self"
    optLang[1].text = "Blank"
    optLang[2].text = "Parent"
    
    document.getElementById("btnCancel").value = "cancel";
    document.getElementById("btnInsert").value = "insert";
    document.getElementById("btnApply").value = "apply";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Hyperlink</title>")
    }