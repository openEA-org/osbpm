function loadTxt()
    {
    var txtLang = document.getElementsByName("txtLang");
    txtLang[0].innerHTML = "Sti";
    txtLang[1].innerHTML = "Baggrund";
    txtLang[2].innerHTML = "Bredde";
    txtLang[3].innerHTML = "H\u00F8jde";   
    txtLang[4].innerHTML = "Kvalitet";      
    txtLang[5].innerHTML = "Justering";
    txtLang[6].innerHTML = "Gentag";
    txtLang[7].innerHTML = "Ja";
    txtLang[8].innerHTML = "Nej";
    
    txtLang[9].innerHTML = "Class ID";
    txtLang[10].innerHTML = "CodeBase";
    txtLang[11].innerHTML = "Plugin side";

    var optLang = document.getElementsByName("optLang");
    optLang[0].text = "Lav"
    optLang[1].text = "H\u00F8j"
    optLang[2].text = "Standard"
    optLang[3].text = "Venstre"
    optLang[4].text = "H\u00F8jre"
    optLang[5].text = "\u00D8verst"
    optLang[6].text = "Nederst"
    
    document.getElementById("btnPick").value = "V\u00E6lg";
    
    document.getElementById("btnCancel").value = "Annuller";
    document.getElementById("btnOk").value = " Ok ";
    }
function getTxt(s)
    {
    switch(s)
        {
        case "Custom Colors": return "Egne farver";
        case "More Colors...": return "Flere farver...";
        default: return "";
        }
    }
function writeTitle()
    {
    document.write("<title>"+"Inds\u00E6t Flash"+"</title>")
    }