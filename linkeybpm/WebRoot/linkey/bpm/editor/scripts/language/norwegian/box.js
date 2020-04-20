function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Farge";
    txtLang[1].innerHTML = "Skygge";    
    
    txtLang[2].innerHTML = "Margin";
    txtLang[3].innerHTML = "Left";
    txtLang[4].innerHTML = "Right";
    txtLang[5].innerHTML = "Top";
    txtLang[6].innerHTML = "Bottom";
    
    txtLang[7].innerHTML = "Padding";
    txtLang[8].innerHTML = "Left";
    txtLang[9].innerHTML = "Right";
    txtLang[10].innerHTML = "Top";
    txtLang[11].innerHTML = "Bottom";

    txtLang[12].innerHTML = "Dimension";
    txtLang[13].innerHTML = "Width";
    txtLang[14].innerHTML = "Height";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels";
    optLang[1].text = "percent";
    optLang[2].text = "pixels";
    optLang[3].text = "percent";

    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnApply").value = "Oppdater";
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
    document.write("<title>Box Formatting</title>")
    }