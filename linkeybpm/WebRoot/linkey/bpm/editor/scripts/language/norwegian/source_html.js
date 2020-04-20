function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Tekstbrytning";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "Search";
        case "Cut":return "Klipp";
        case "Copy":return "Kopier";
        case "Paste":return "Lim inn";
        case "Undo":return "Angre";
        case "Redo":return "Avbryt angre";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Kildekode editor</title>")
    }
