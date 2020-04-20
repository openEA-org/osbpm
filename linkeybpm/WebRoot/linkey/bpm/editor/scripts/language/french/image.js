function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Source";
    txtLang[1].innerHTML = "Texte au survol<br>de la souris";
    txtLang[2].innerHTML = "Espacements";
    txtLang[3].innerHTML = "Alignement";
    txtLang[4].innerHTML = "Haut";
    txtLang[5].innerHTML = "Bordure d\u0027image";
    txtLang[6].innerHTML = "Bas";
    txtLang[7].innerHTML = "Largeur";
    txtLang[8].innerHTML = "Gauche";
    txtLang[9].innerHTML = "Hauteur";
    txtLang[10].innerHTML = "Droite";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "absBas";
    optLang[1].text = "absMilieu";
    optLang[2].text = "ligne de base";
    optLang[3].text = "bas";
    optLang[4].text = "gauche";
    optLang[5].text = "milieu";
    optLang[6].text = "droite";
    optLang[7].text = "d\u00E9but du texte";
    optLang[8].text = "haut";
 
    document.getElementById("btnBorder").value = " style de bordure ";
    document.getElementById("btnReset").value = "r\u00E9initialiser";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function writeTitle()
    {
    document.write("<title>Insertion d\u0027une image&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</title>")
    }