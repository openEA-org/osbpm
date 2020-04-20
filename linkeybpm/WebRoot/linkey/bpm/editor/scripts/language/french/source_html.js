function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Renvoi \u00E0 la ligne";
    document.getElementById("btnCancel").value = "Annuler";
    document.getElementById("btnApply").value = "Actualiser";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "rechercher";
        case "Cut":return "Couper";
        case "Copy":return "Copier";
        case "Paste":return "Coller";
        case "Undo":return "Annuler l\u0027action";
        case "Redo":return "R\u00E9tablir l\u0027action annul\u00E9e";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Editeur Code HTML</title>")
    }
