function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Justifier";
    txtLang[1].innerHTML = "Propri\u00E9t\u00E9s";
    txtLang[2].innerHTML = "Style";
    txtLang[3].innerHTML = "Largeur";
    txtLang[4].innerHTML = "Justifier le contenu du tableau";
    txtLang[5].innerHTML = "Fixer la largeur du tableau";
    txtLang[6].innerHTML = "Justifier la fen\u00EAtre";
    txtLang[7].innerHTML = "Hauteur";
    txtLang[8].innerHTML = "Justifier le contenu du tableau";
    txtLang[9].innerHTML = "Fixer la hauteur du tableau";
    txtLang[10].innerHTML = "Justifier la fen\u00EAtre";
    txtLang[11].innerHTML = "Alignement";
    txtLang[12].innerHTML = "Marge";
    txtLang[13].innerHTML = "Gauche";
    txtLang[14].innerHTML = "Droit";
    txtLang[15].innerHTML = "Haut";
    txtLang[16].innerHTML = "Bas";  
    txtLang[17].innerHTML = "Bordures";
    txtLang[18].innerHTML = "Collapse";
    txtLang[19].innerHTML = "Arri\u00E8re plan";
    txtLang[20].innerHTML = "Espace entre cellules";
    txtLang[21].innerHTML = "Retrait de cellule";
    txtLang[22].innerHTML = "Texte CSS";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "pourcentage"
    optLang[2].text = "pixels"
    optLang[3].text = "pourcentage"
    optLang[4].text = "gauche"
    optLang[5].text = "centre"
    optLang[6].text = "droit"
    optLang[7].text = "sans bordure"
    optLang[8].text = "Oui"
    optLang[9].text = "Non"

    document.getElementById("btnPick").value="Couleur";
    document.getElementById("btnImage").value="Image";

    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Propri\u00E9t\u00E9s du tableau</title>")
    }