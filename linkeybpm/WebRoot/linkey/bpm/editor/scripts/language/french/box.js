function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Couleur";
    txtLang[1].innerHTML = "Ombrage";   
    
    txtLang[2].innerHTML = "Marge";
    txtLang[3].innerHTML = "Gauche";
    txtLang[4].innerHTML = "Droite";
    txtLang[5].innerHTML = "Haut";
    txtLang[6].innerHTML = "Bas";
    
    txtLang[7].innerHTML = "Espace";
    txtLang[8].innerHTML = "Gauche";
    txtLang[9].innerHTML = "Droite";
    txtLang[10].innerHTML = "Haut";
    txtLang[11].innerHTML = "Bas";

    txtLang[12].innerHTML = "Dimension";
    txtLang[13].innerHTML = "Largeur";
    txtLang[14].innerHTML = "Hauteur";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "px";
    optLang[1].text = "pourcentage";
    optLang[2].text = "px";
    optLang[3].text = "pourcentage";

    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Appliquer";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Sans bordure";
        case "Outside Border": return "Bordures Externes";
        case "Left Border": return "Bordure Gauche";
        case "Top Border": return "bordure Haute";
        case "Right Border": return "Bordure Droite";
        case "Bottom Border": return "Bordure Basse";
        case "Pick": return "Choisir";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Box Formatting&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }