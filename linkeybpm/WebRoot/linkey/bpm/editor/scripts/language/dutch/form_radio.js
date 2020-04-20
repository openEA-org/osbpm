function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Naam";
    txtLang[1].innerHTML = "Waarde";
    txtLang[2].innerHTML = "Standaard";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Geselecteerd"
    optLang[1].text = "Niet Geselecteerd"
    
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Keuzerondje</title>")
    }