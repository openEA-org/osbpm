function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Navn";
    txtLang[2].innerHTML = "V\u00E6rdi";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Normal"
    optLang[1].text = "Send"
    optLang[2].text = "Nulstil"
        
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Formularknap</title>")
    }