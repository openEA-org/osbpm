function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Kleur";
    txtLang[1].innerHTML = "Schaduw";   
    
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
        
    document.getElementById("btnCancel").value = "annuleren";
    document.getElementById("btnApply").value = "toepassen";
    document.getElementById("btnOk").value = " ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "No Border": return "Geen rand";
        case "Outside Border": return "Rand buiten";
        case "Left Border": return "Rand links";
        case "Top Border": return "Rand boven";
        case "Right Border": return "Rand rechts";
        case "Bottom Border": return "Rand onder";
        case "Pick": return "Kiezen";
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>Box Formatting</title>")
    }