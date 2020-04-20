function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Type";
    txtLang[1].innerHTML = "Nom";
    txtLang[2].innerHTML = "Taille";
    txtLang[3].innerHTML = "longueur Maxi";
    txtLang[4].innerHTML = "Nbr. de Lignes";
    txtLang[5].innerHTML = "Valeur";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Texte"
    optLang[1].text = "Zone de Texte"
    optLang[2].text = "Mot de Passe"
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Champ Texte</title>")
    }