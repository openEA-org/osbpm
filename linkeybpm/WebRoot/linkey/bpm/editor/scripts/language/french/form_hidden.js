function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Nom";
    txtLang[1].innerHTML = "Valeur";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Champ Cach\u00E9</title>")
    }
