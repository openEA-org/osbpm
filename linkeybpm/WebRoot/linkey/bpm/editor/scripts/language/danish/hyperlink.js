function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Bogm\u00E6rke";
    txtLang[2].innerHTML = "Ramme";
    txtLang[3].innerHTML = "Titel";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Samme"
    optLang[1].text = "Nyt vindue"
    optLang[2].text = "Overordnet"
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Link</title>")
    }