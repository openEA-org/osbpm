function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "F\u00E4rg";
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Ingen kantlinje";
        case "Outside Border": return "Yttre kantlinje";
        case "Left Border": return "V\u00E4nster kantlinje";
        case "Top Border": return "\u00D6vre kantlinje";
        case "Right Border": return "H\u00F6ger kantlinje";
        case "Bottom Border": return "Nedre kantlinje";
        case "Pick": return "V\u00E4lj";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Kantlinjer</title>")
    }