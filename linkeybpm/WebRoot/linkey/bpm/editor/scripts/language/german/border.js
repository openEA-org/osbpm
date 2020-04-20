function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "Farbe";
    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "kein Rahmen";
        case "Outside Border": return "Rahmen aussen";
        case "Left Border": return "Rahmenlinie links";
        case "Top Border": return "Rahmenlinie oben";
        case "Right Border": return "Rahmenlinie rechts";
        case "Bottom Border": return "Rahmenlinie unten";
        case "Pick": return "w\u00E4hlen"; //"Pick";
        case "Custom Colors": return "Benutzerfarben";
        case "More Colors...": return "weitere Farben...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Rahmen</title>")
    }