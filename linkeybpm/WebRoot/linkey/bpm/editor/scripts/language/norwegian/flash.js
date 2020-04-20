function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Bakgrunn";
    txtLang[2].innerHTML = "Bredde";
    txtLang[3].innerHTML = "H\u00F8yde";
    txtLang[4].innerHTML = "Kvalitet";
    txtLang[5].innerHTML = "Justering";
    txtLang[6].innerHTML = "Gjenta";
    txtLang[7].innerHTML = "Ja";
    txtLang[8].innerHTML = "Nei";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "Plugin side";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Lav"
    optLang[1].text = "H\u00F8y"
    optLang[2].text = "Standard"
    optLang[3].text = "Venstre"
    optLang[4].text = "H\u00F8yre"
    optLang[5].text = "\u00D8verst"
    optLang[6].text = "Nederst"
    
    document.getElementById("btnPick").value = "Velg";
    
    document.getElementById("btnCancel").value = "Avbryt";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Custom Colors";
        case "More Colors...": return "More Colors...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>"+"Sett inn Flash"+"</title>")
    }