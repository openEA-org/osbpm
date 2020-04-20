function loadTxt()
    {    
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "justifier";
    txtLang[1].innerHTML = "Propri\u00E9t\u00E9s";
    txtLang[2].innerHTML = "Style";
    txtLang[3].innerHTML = "Largeur";
    txtLang[4].innerHTML = "Justifier le contenu";
    txtLang[5].innerHTML = "Fixer la largeur de cellule";
    txtLang[6].innerHTML = "Hauteur";
    txtLang[7].innerHTML = "Justifier le contenu";
    txtLang[8].innerHTML = "Fixer la hauteur de cellule";
    txtLang[9].innerHTML = "Alignement du Texte";
    txtLang[10].innerHTML = "Marge";
    txtLang[11].innerHTML = "Gauche";
    txtLang[12].innerHTML = "Droite";
    txtLang[13].innerHTML = "Haut";
    txtLang[14].innerHTML = "Bas";
    txtLang[15].innerHTML = "Retrait";
    txtLang[16].innerHTML = "Arri\u00E8re plan";
    txtLang[17].innerHTML = "Aper\u00E7u";
    txtLang[18].innerHTML = "Texte CSS";
    txtLang[19].innerHTML = "Appliqu\u00E9 \u00E0";

    document.getElementById("btnPick").value = "Couleur";
    document.getElementById("btnImage").value = "Image";
    document.getElementById("btnText").value = "Mise en Forme du Texte";
    document.getElementById("btnBorder").value = "Epaisseur de la Bordure";

    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels"
    optLang[1].text = "pourcentage"
    optLang[2].text = "pixels"
    optLang[3].text = "pourcentage"
    optLang[4].text = "aucun"
    optLang[5].text = "haut"
    optLang[6].text = "milieu"
    optLang[7].text = "bas"
    optLang[8].text = "ligne de base"
    optLang[9].text = "dessous"
    optLang[10].text = "dessus"
    optLang[11].text = "d\u00E9but texte"
    optLang[12].text = "fin texte"
    optLang[13].text = "aucun"
    optLang[14].text = "gauche"
    optLang[15].text = "centre"
    optLang[16].text = "droit"
    optLang[17].text = "justifi\u00E9"
    optLang[18].text = "aucun"
    optLang[19].text = "sans bordure"
    optLang[20].text = "relief"
    optLang[21].text = "Normal"
    optLang[22].text = "Cellule courante"
    optLang[23].text = "Ligne courante"
    optLang[24].text = "Colonne courante"
    optLang[25].text = "Whole Table"
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Propri\u00E9t\u00E9s d\u0027une cellule</title>")
    }