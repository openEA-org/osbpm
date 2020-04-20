function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Num\u00E9ros";
    txtLang[1].innerHTML = "Avec puces";
    txtLang[2].innerHTML = "Num\u00E9ro de d\u00E9part";
    txtLang[3].innerHTML = "Marge gauche";
    txtLang[4].innerHTML = "Utiliser une image "
    txtLang[5].innerHTML = "Marge gauche";
    
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";  
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Please select a list.":return "Veuillez s\u00E9lectionner une liste.";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Format d\u0027une Liste</title>");
    //document.width = "600";
    }