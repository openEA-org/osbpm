function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Navn";
    txtLang[2].innerHTML = "St\u00F8rrelse";
    txtLang[3].innerHTML = "Max L\u00E6ngde";
    txtLang[4].innerHTML = "Linjer";
    txtLang[5].innerHTML = "V\u00E6rdi";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Tekstbox"
    optLang[1].text = "Tekstfelt"
    optLang[2].text = "Adgangskode"
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Tekstboks</title>")
    }