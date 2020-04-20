function loadTxt()
    {
    document.getElementById("txtLang").innerHTML = "V\u00E4ri";
    document.getElementById("btnCancel").value = "Peruuta";
    document.getElementById("btnOk").value = " OK ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Ei kehyksi\u00E4";
        case "Outside Border": return "Ulkokehys";
        case "Left Border": return "Vasen kehys";
        case "Top Border": return "Yl\u00E4kehys";
        case "Right Border": return "Oikea kehys";
        case "Bottom Border": return "Alakehys";
        case "Pick": return "Poimi";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Kehykset</title>")
    }