function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Alignement";
    txtLang[1].innerHTML = "Indentation";
    txtLang[2].innerHTML = "Espacement des mots";
    txtLang[3].innerHTML = "Espacement des Caract\u00E8res";
    txtLang[4].innerHTML = "Hauteur de la ligne";
    txtLang[5].innerHTML = "Casse du texte";
    txtLang[6].innerHTML = "Espace blanc";
    
    document.getElementById("divPreview").innerHTML = "Lorem ipsum dolor sit amet, " +
        "consetetur sadipscing elitr, " +
        "sed diam nonumy eirmod tempor invidunt ut labore et " +
        "dolore magna aliquyam erat, " +
        "sed diam voluptua. At vero eos et accusam et justo " +
        "duo dolores et ea rebum. Stet clita kasd gubergren, " +
        "no sea takimata sanctus est Lorem ipsum dolor sit amet.";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Sans action";
    optLang[1].text = "gauche";
    optLang[2].text = "Droite";
    optLang[3].text = "Centre";
    optLang[4].text = "Justifi\u00E9";
    optLang[5].text = "Sans action";
    optLang[6].text = "Capitale";
    optLang[7].text = "Majuscule";
    optLang[8].text = "Minuscule";
    optLang[9].text = "Aucun";
    optLang[10].text = "Sans action";
    optLang[11].text = "Sans renvoi";
    optLang[12].text = "Relief";
    optLang[13].text = "Normal";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";  
    }
function writeTitle()
    {
    document.write("<title>Mise en forme du paragraphe courant</title>")
    }
