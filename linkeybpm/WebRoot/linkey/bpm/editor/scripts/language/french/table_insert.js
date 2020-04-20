function loadTxt()
    {
    var txtLang =  document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Lignes";
    txtLang[1].innerHTML = "Espacement";
    txtLang[2].innerHTML = "Colonnes";
    txtLang[3].innerHTML = "Retrait";
    txtLang[4].innerHTML = "Bordures";
    txtLang[5].innerHTML = "Collapse";
    
    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Sans Bordure";
    optLang[1].text = "Oui";
    optLang[2].text = "Non";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnInsert").value = "Ins\u00E9rer";

    document.getElementById("btnSpan1").value = "Span v";
    document.getElementById("btnSpan2").value = "Span >";
    }
function writeTitle()
    {
    document.write("<title>Ins\u00E9rer un Tableau</title>")
    }