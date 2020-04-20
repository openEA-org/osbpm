function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "Verdi";
    txtLang[2].innerHTML = "Starttilstand";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Valgt"
    optLang[1].text = "Ikke valgt"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Alternativknapp</title>")
    }