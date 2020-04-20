function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "St\u00F8rrelse";
    txtLang[2].innerHTML = "Tillat flere valg";
    txtLang[3].innerHTML = "Verdier";
    
    document.getElementById("btnAdd").value = "  tilf\u00F8y  ";
    document.getElementById("btnUp").value = "  Opp  ";
    document.getElementById("btnDown").value = "  Ned  ";
    document.getElementById("btnDel").value = "  Slett  ";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Rulleliste</title>")
    }