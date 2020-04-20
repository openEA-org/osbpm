function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Radbrytning";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "S\u00F6k";
        case "Cut":return "Klipp ut";
        case "Copy":return "Kopiera";
        case "Paste":return "Klistra in";
        case "Undo":return "\u00C5ngra";
        case "Redo":return "G\u00F6r om";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>" + "K\u00E4llkods editor" + "</title>")
    }
