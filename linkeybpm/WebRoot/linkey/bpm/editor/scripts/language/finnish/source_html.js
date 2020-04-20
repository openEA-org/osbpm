function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Sisenn\u00E4 teksti";
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnApply").value = "K\u00E4yt\u00E4";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Search":return "Search";
        case "Cut":return "Leikkaa";
        case "Copy":return "Kopioi";
        case "Paste":return "Liit&auml;";
        case "Undo":return "Kumoa";
        case "Redo":return "Toista";
        default:return "";
        }
    }
function writeTitle()
    {
    document.write("<title>"+"L\u00E4hde-editori"+"</title>")
    }
