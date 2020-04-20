function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "V\u00E6rdi";
    txtLang[2].innerHTML = "Starttilstand";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Markeret"
    optLang[1].text = "Ikke markeret"
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnInsert").value = "Inds\u00E6t";
    document.getElementById("btnApply").value = "Tilf\u00F8j";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Afkrydsningsfelt</title>")
    }