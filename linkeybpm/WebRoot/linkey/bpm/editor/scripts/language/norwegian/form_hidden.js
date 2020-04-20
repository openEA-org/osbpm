function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Navn";
    txtLang[1].innerHTML = "Verdi";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnInsert").value = "Sett inn";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function writeTitle()
    {
    document.write("<title>Skjult felt</title>")
    }