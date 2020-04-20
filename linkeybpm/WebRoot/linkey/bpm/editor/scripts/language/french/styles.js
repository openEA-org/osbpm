function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Styles";
    txtLang[1].innerHTML = "Aper\u00E7u";
    txtLang[2].innerHTML = "Appliquer";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "au texte s\u00E9lectionn\u00E9";
    optLang[1].text = "\u00E0 la balise courante";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";      
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "Vous s\u00E9lectionnez une balise BODY.";
        case "Please select a text.":
            return "Vous devez s\u00E9lectionner un texte.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Styles</title>");
    }
