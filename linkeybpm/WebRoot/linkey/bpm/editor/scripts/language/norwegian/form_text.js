function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Navn";
    txtLang[2].innerHTML = "St\u00F8rrelse";
    txtLang[3].innerHTML = "Max Lengde";
    txtLang[4].innerHTML = "Linjer";
    txtLang[5].innerHTML = "Verdi";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Tekstboks"
    optLang[1].text = "Tekstfelt"
    optLang[2].text = "Adgangskode"
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Tekstboks</title>")
    }