function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Naam";
    txtLang[1].innerHTML = "Aktie";
    txtLang[2].innerHTML = "Methode";
        
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnInsert").value = "invoegen";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Formulier</title>")
    }