function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Automatische terugloop";
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "Search";
        case "Cut":return "Knippen";
        case "Copy":return "Kopi&euml;ren";
        case "Paste":return "Plakken";
        case "Undo":return "Ongedaan maken";
        case "Redo":return "Opnieuw";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Bron Bewerken</title>")
    }
