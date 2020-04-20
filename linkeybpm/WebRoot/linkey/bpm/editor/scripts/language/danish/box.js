function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Farve";
    txtLang[1].innerHTML = "Skygge";    
    
    txtLang[2].innerHTML = "Margen";
    txtLang[3].innerHTML = "Venstre";
    txtLang[4].innerHTML = "H\u00F8jre";
    txtLang[5].innerHTML = "Top";
    txtLang[6].innerHTML = "Nederst";
    
    txtLang[7].innerHTML = "Padding";
    txtLang[8].innerHTML = "Venstre";
    txtLang[9].innerHTML = "H\u00F8jre";
    txtLang[10].innerHTML = "Top";
    txtLang[11].innerHTML = "Nederst";

    txtLang[12].innerHTML = "St\u00F8rrelse";
    txtLang[13].innerHTML = "Bredde";
    txtLang[14].innerHTML = "H\u00F8jde";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "pixels";
    optLang[1].text = "procent";
    optLang[2].text = "pixels";
    optLang[3].text = "procent";

    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnApply").value = "Opdater";
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
    document.write("<title>Ramme formatering</title>")
    }
