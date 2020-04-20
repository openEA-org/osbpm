function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Texte CSS";
    txtLang[1].innerHTML = "Nom de la Classe";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "You're selecting BODY element.":
            return "You\u0027re selecting BODY element.";
        case "Please select a text.":
            return "Vous devez s\u00E9lectionner un texte.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Feuille de Style CSS Personnalis\u00E9e</title>")
    }