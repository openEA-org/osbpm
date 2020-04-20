function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Textumbruch";
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "Suchen";
        case "Cut":return "Ausschneiden";
        case "Copy":return "Kopieren";
        case "Paste":return "Einf&uuml;gen";
        case "Undo":return "R&uuml;ckg&auml;ngig";
        case "Redo":return "Wiederherstellen";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Quelltext Editor</title>")
    }
