function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Farbe";
    txtLang[1].innerHTML = "Schattierung";  
    
    txtLang[2].innerHTML = "Aussenabstand";
    txtLang[3].innerHTML = "links";
    txtLang[4].innerHTML = "rechts";
    txtLang[5].innerHTML = "oben";
    txtLang[6].innerHTML = "unten";
    
    txtLang[7].innerHTML = "Innenabstand";
    txtLang[8].innerHTML = "links";
    txtLang[9].innerHTML = "rechts";
    txtLang[10].innerHTML = "oben";
    txtLang[11].innerHTML = "unten";

    txtLang[12].innerHTML = "Dimension";
    txtLang[13].innerHTML = "Breite";
    txtLang[14].innerHTML = "H&ouml;he";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixel";
    optLang[1].text = "Prozent";
    optLang[2].text = "Pixel";
    optLang[3].text = "Prozent";

    document.getElementById("btnCancel").value = "Abbrechen";
    document.getElementById("btnApply").value = "\u00DCbernehmen"; //"apply";
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
    document.write("<title>Box Formatierung</title>")
    }