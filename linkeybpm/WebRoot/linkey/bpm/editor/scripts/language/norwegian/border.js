function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Farge";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Ingen";
        case "Outside Border": return "Utvendig kant";
        case "Left Border": return "Venstre kant";
        case "Top Border": return "\u00D8verste kant";
        case "Right Border": return "H\u00F8yre kant";
        case "Bottom Border": return "Nederste kant";
        case "Pick": return "Velg";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Ramme</title>")
    }