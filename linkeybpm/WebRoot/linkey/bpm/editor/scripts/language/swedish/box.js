function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "F\u00E4rg";
    txtLang[1].innerHTML = "Fyllning";      
    
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
    document.getElementById("btnApply").value = "Verkst\u00E4ll";
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
    document.write("<title>Box Formatting</title>")
    }