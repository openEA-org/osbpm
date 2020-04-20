function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Nom";
    txtLang[2].innerHTML = "Valeur";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Bouton"
    optLang[1].text = "Envoyer"
    optLang[2].text = "Annuler"
        
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Bouton</title>")
    }