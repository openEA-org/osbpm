function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Tekstombrydning";
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "S\u00F8g";
        case "Cut":return "Klip";
        case "Copy":return "Kopier";
        case "Paste":return "Inds&aelig;t";
        case "Undo":return "Fortryd";
        case "Redo":return "Annuler fortryd";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Kildekode editor</title>")
    }
