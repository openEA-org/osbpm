function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Farve";
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Ingen";
        case "Outside Border": return "Udvendig kant";
        case "Left Border": return "Venstre kant";
        case "Top Border": return "\u00D8verste kant";
        case "Right Border": return "H\u00F8jre kant";
        case "Bottom Border": return "Nederste kant";
        case "Pick": return "V\u00E6lg";
        case "Custom Colors": return "Egne farver";
        case "More Colors...": return "Flere farver...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Ramme</title>")
    }